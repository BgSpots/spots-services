package com.spots.service.auth;

import com.mongodb.DuplicateKeyException;
import com.spots.common.GenericValidator;
import com.spots.common.input.LoginBody;
import com.spots.common.input.RegisterBody;
import com.spots.common.output.FacebookUserDTO;
import com.spots.common.output.GoogleUserDTO;
import com.spots.common.output.LoginResponse;
import com.spots.domain.Role;
import com.spots.domain.User;
import com.spots.domain.VerificationCode;
import com.spots.repository.UserRepository;
import com.spots.repository.VerificationCodeRepository;
import com.spots.service.common.SequenceGeneratorService;
import com.spots.service.user.InvalidUserException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final GenericValidator<LoginBody> loginValidator = new GenericValidator<>();
    private final GenericValidator<RegisterBody> registerValidator = new GenericValidator<>();
    private final RedisTemplate<String, String> redis;
    private final JavaMailSender mailSender;
    private final String defaultProfilePictureName = "default-profile.png";

    @Transactional
    public void register(RegisterBody body) throws MessagingException {
        registerValidator.validate(body);
        var user =
                User.builder()
                        .id(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME))
                        .username(extractUsername(body.getEmail()))
                        .email(body.getEmail())
                        .role(Role.USER)
                        .password(body.getPassword())
                        .conqueredSpots(new ArrayList<>())
                        .build();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setImageName(defaultProfilePictureName);
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new EmailTakenException("User with that email already exists");
        }
        sendVerificationEmail(user.getEmail());
        userRepository.insert(user);
    }

    @Transactional
    public LoginResponse login(LoginBody body) {
        loginValidator.validate(body);
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword()));
        // get user
        var optionalUser = userRepository.findUserByEmail(body.getEmail());
        if (optionalUser.isEmpty()
                || !passwordEncoder.matches(body.getPassword(), optionalUser.get().getPassword()))
            throw new InvalidLoginCredenials("User with that email and password does not exist!");
        var user = optionalUser.get();
        if (!user.isEmailVerified()) throw new EmailNotVerifiedException("Email is not verified");
        user.setUsername(body.getEmail());
        var jwtToken = jwtService.generateToken(user);
        // return jwt token to client
        final var timeUntilNextRoll =
                user.getNextRandomSpotGeneratedTime() == null
                        ? Duration.ZERO
                        : Duration.between(LocalDateTime.now(), user.getNextRandomSpotGeneratedTime());
        return LoginResponse.builder()
                .accessToken(jwtToken)
                .timeUntilNextRoll(timeUntilNextRoll.getSeconds())
                .currentSpotId(user.getCurrentSpotId())
                .imageName(user.getImageName())
                .id(user.getId())
                .build();
    }

    @Transactional
    public GoogleUserDTO loginWithGoogle(String accessToken) {
        WebClient webClient = WebClient.create();
        try {
            final var googleUserDTO =
                    webClient
                            .get()
                            .uri("https://www.googleapis.com/userinfo/v2/me")
                            .header("Authorization", "Bearer " + accessToken)
                            .retrieve()
                            .bodyToMono(GoogleUserDTO.class)
                            .block();
            final var userOptional = userRepository.findUserByEmail(googleUserDTO.getEmail());
            User user;
            if (!userOptional.isPresent()) {
                user =
                        User.builder()
                                .id(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME))
                                .username(extractUsername(googleUserDTO.getEmail()))
                                .email(googleUserDTO.getEmail())
                                .role(Role.USER)
                                .imageName(googleUserDTO.getImageUrl())
                                .emailVerified(true)
                                .conqueredSpots(new ArrayList<>())
                                .build();
            } else {
                user = userOptional.get();
            }
            googleUserDTO.setJwtToken(jwtService.generateToken(user));
            final var timeUntilNextRoll =
                    user.getNextRandomSpotGeneratedTime() == null
                            ? Duration.ZERO
                            : Duration.between(LocalDateTime.now(), user.getNextRandomSpotGeneratedTime());
            googleUserDTO.setTimeUntilNextRoll(timeUntilNextRoll);
            googleUserDTO.setId(String.valueOf(user.getId()));
            googleUserDTO.setCurrentSpotId(user.getCurrentSpotId());

            if (userRepository.existsUserByEmail(user.getEmail())) {
                return googleUserDTO;
            } else {
                userRepository.insert(user);
            }
            return googleUserDTO;
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyExistsException("User already exists!");
        } catch (Exception e) {
            throw new InvalidAccessTokenException("Invalid access token: " + accessToken);
        }
    }

    @Transactional
    public FacebookUserDTO loginWithFacebook(String accessToken) {
        WebClient webClient = WebClient.create();
        try {
            final var facebookUserDTO =
                    webClient
                            .get()
                            .uri(
                                    "https://graph.facebook.com/me?access_token="
                                            + accessToken
                                            + "&fields=id,name,email,picture.type(large)")
                            .retrieve()
                            .bodyToMono(FacebookUserDTO.class)
                            .block();

            final var userOptional = userRepository.findUserByEmail(facebookUserDTO.getEmail());
            User user;
            if (!userOptional.isPresent()) {
                user =
                        User.builder()
                                .id(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME))
                                .username(facebookUserDTO.getName())
                                .email(facebookUserDTO.getEmail())
                                .role(Role.USER)
                                .imageName(facebookUserDTO.getImageUrl().getData().getUrl())
                                .emailVerified(true)
                                .conqueredSpots(new ArrayList<>())
                                .build();
            } else {
                user = userOptional.get();
            }
            facebookUserDTO.setJwtToken(jwtService.generateToken(user));
            facebookUserDTO.setId(user.getId());
            final var timeUntilNextRoll =
                    user.getNextRandomSpotGeneratedTime() == null
                            ? Duration.ZERO
                            : Duration.between(LocalDateTime.now(), user.getNextRandomSpotGeneratedTime());
            facebookUserDTO.setTimeUntilNextRoll(timeUntilNextRoll);
            facebookUserDTO.setCurrentSpotId(user.getCurrentSpotId());
            if (userRepository.existsUserByEmail(user.getEmail())) {
                return facebookUserDTO;
            } else {
                userRepository.insert(user);
            }
            return facebookUserDTO;
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyExistsException("User already exists!");
        } catch (Exception e) {
            throw new InvalidAccessTokenException("Invalid access token: " + accessToken);
        }
    }

    public void logout(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        var jwt = authHeader.substring(7);
        redis.opsForValue().set(request.getRemoteAddr(), jwt);
    }

    @Transactional
    public void verifyEmail(String code) {
        final var verificationCode =
                verificationCodeRepository
                        .findVerificationCodeByCode(code)
                        .orElseThrow(() -> new InvalidVerificationCodeException("Invalid verification code!"));
        final var user =
                userRepository
                        .findUserByEmail(verificationCode.getEmail())
                        .orElseThrow(
                                () -> new InvalidUserException("Verification code does not match any user"));
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    public void sendVerificationEmail(String userEmail) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(userEmail);
        helper.setFrom("bgspots@gmail.com");
        helper.setSubject("Spots Verification Code");

        String verificationCode = generateRandomCode(userEmail); // Generate code based on email
        String emailContent = "Your verification code is: <strong>" + verificationCode + "</strong>";

        helper.setText(emailContent, true);

        final var code =
                VerificationCode.builder()
                        .code(verificationCode)
                        .email(userEmail)
                        .id(sequenceGeneratorService.generateSequence(VerificationCode.SEQUENCE_NAME))
                        .build();
        verificationCodeRepository.insert(code);
        mailSender.send(message);
    }

    private String generateRandomCode(String userEmail) {
        // Use the user's email as part of the seed for generating the code
        String seed = userEmail + System.currentTimeMillis();

        // Generate the code using the seed
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random(seed.hashCode());

        for (int i = 0; i < 6; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }

        return code.toString();
    }

    private static String extractUsername(String emailAddress) {
        String regexPattern = "^(.+)@.+$";

        Pattern pattern = Pattern.compile(regexPattern);

        Matcher matcher = pattern.matcher(emailAddress);

        if (matcher.find()) {
            return matcher.group(1); // Group 1 contains the first part (username)
        } else {
            return null; // Return null if no match is found
        }
    }
}
