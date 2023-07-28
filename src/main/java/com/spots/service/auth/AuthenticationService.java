package com.spots.service.auth;

import com.mongodb.DuplicateKeyException;
import com.spots.common.GenericValidator;
import com.spots.common.auth.LoginBody;
import com.spots.common.auth.LoginResponse;
import com.spots.common.auth.RegisterBody;
import com.spots.domain.Role;
import com.spots.domain.User;
import com.spots.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final RedisTemplate<String, String> redis;

    public LoginResponse register(RegisterBody body) {
        var user =
                User.builder().email(body.getEmail()).role(Role.USER).password(body.getPassword()).build();

        GenericValidator<User> spotValidator = new GenericValidator<>();
        spotValidator.validate(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new EmailTakenException("User with that email already exists");
        }
        var jwtToken = jwtService.generateToken(user);
        userRepository.insert(user);
        return LoginResponse.builder().accessToken(jwtToken).build();
    }

    public LoginResponse login(LoginBody body) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword()));
        // get user
        var user = userRepository.findUserByEmail(body.getEmail());
        if (user.isEmpty() || !passwordEncoder.matches(body.getPassword(), user.get().getPassword()))
            throw new InvalidLoginCredenials("User with that email and password does not exist!");
        var jwtToken = jwtService.generateToken(user.get());
        // return jwt token to client
        return LoginResponse.builder().accessToken(jwtToken).build();
    }

    public LoginResponse loginWithGoogle(String accessToken) {
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
            final var user =
                    User.builder()
                            .username(googleUserDTO.getName())
                            .id(googleUserDTO.getId())
                            .email(googleUserDTO.getEmail())
                            .picture(googleUserDTO.getPicture())
                            .build();
            userRepository.insert(user);
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyExistsException("User already exists!");
        } catch (Exception e) {
            throw new InvalidAccessTokenException("Invalid access token: " + accessToken);
        }
        return null;
    }

    public void logout(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        var jwt = authHeader.substring(7);
        redis.opsForValue().set(request.getRemoteAddr(), jwt);
    }
}
