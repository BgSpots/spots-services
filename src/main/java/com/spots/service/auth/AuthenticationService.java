package com.spots.service.auth;

import static com.spots.service.user.UserService.userId;

import com.mongodb.DuplicateKeyException;
import com.spots.common.GenericValidator;
import com.spots.common.input.LoginBody;
import com.spots.common.input.RegisterBody;
import com.spots.common.output.FacebookUserDTO;
import com.spots.common.output.GoogleUserDTO;
import com.spots.common.output.LoginResponse;
import com.spots.domain.Role;
import com.spots.domain.User;
import com.spots.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
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
                User.builder()
                        .username(body.getEmail())
                        .email(body.getEmail())
                        .role(Role.USER)
                        .password(body.getPassword())
                        .build();

        GenericValidator<User> spotValidator = new GenericValidator<>();
        spotValidator.validate(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPicture(
                "data:image/png;base64,/9j/4AAQSkZJRgABAQEBLAEsAAD/4QBpRXhpZgAASUkqAAgAAAABAA4BAgBHAAAAGgAAAAAAAABEZWZhdWx0IHByb2ZpbGUgcGljdHVyZSwgYXZhdGFyLCBwaG90byBwbGFjZWhvbGRlci4gVmVjdG9yIGlsbHVzdHJhdGlvbv/hBWtodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+Cjx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iPgoJPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4KCQk8cmRmOkRlc2NyaXB0aW9uIHJkZjphYm91dD0iIiB4bWxuczpwaG90b3Nob3A9Imh0dHA6Ly9ucy5hZG9iZS5jb20vcGhvdG9zaG9wLzEuMC8iIHhtbG5zOklwdGM0eG1wQ29yZT0iaHR0cDovL2lwdGMub3JnL3N0ZC9JcHRjNHhtcENvcmUvMS4wL3htbG5zLyIgICB4bWxuczpHZXR0eUltYWdlc0dJRlQ9Imh0dHA6Ly94bXAuZ2V0dHlpbWFnZXMuY29tL2dpZnQvMS4wLyIgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIiB4bWxuczpwbHVzPSJodHRwOi8vbnMudXNlcGx1cy5vcmcvbGRmL3htcC8xLjAvIiAgeG1sbnM6aXB0Y0V4dD0iaHR0cDovL2lwdGMub3JnL3N0ZC9JcHRjNHhtcEV4dC8yMDA4LTAyLTI5LyIgeG1sbnM6eG1wUmlnaHRzPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvcmlnaHRzLyIgcGhvdG9zaG9wOkNyZWRpdD0iR2V0dHkgSW1hZ2VzL2lTdG9ja3Bob3RvIiBHZXR0eUltYWdlc0dJRlQ6QXNzZXRJRD0iMTIyMzY3MTM5MiIgeG1wUmlnaHRzOldlYlN0YXRlbWVudD0iaHR0cHM6Ly93d3cuaXN0b2NrcGhvdG8uY29tL2xlZ2FsL2xpY2Vuc2UtYWdyZWVtZW50P3V0bV9tZWRpdW09b3JnYW5pYyZhbXA7dXRtX3NvdXJjZT1nb29nbGUmYW1wO3V0bV9jYW1wYWlnbj1pcHRjdXJsIiA+CjxkYzpjcmVhdG9yPjxyZGY6U2VxPjxyZGY6bGk+Y3VtYWNyZWF0aXZlPC9yZGY6bGk+PC9yZGY6U2VxPjwvZGM6Y3JlYXRvcj48ZGM6ZGVzY3JpcHRpb24+PHJkZjpBbHQ+PHJkZjpsaSB4bWw6bGFuZz0ieC1kZWZhdWx0Ij5EZWZhdWx0IHByb2ZpbGUgcGljdHVyZSwgYXZhdGFyLCBwaG90byBwbGFjZWhvbGRlci4gVmVjdG9yIGlsbHVzdHJhdGlvbjwvcmRmOmxpPjwvcmRmOkFsdD48L2RjOmRlc2NyaXB0aW9uPgo8cGx1czpMaWNlbnNvcj48cmRmOlNlcT48cmRmOmxpIHJkZjpwYXJzZVR5cGU9J1Jlc291cmNlJz48cGx1czpMaWNlbnNvclVSTD5odHRwczovL3d3dy5pc3RvY2twaG90by5jb20vcGhvdG8vbGljZW5zZS1nbTEyMjM2NzEzOTItP3V0bV9tZWRpdW09b3JnYW5pYyZhbXA7dXRtX3NvdXJjZT1nb29nbGUmYW1wO3V0bV9jYW1wYWlnbj1pcHRjdXJsPC9wbHVzOkxpY2Vuc29yVVJMPjwvcmRmOmxpPjwvcmRmOlNlcT48L3BsdXM6TGljZW5zb3I+CgkJPC9yZGY6RGVzY3JpcHRpb24+Cgk8L3JkZjpSREY+CjwveDp4bXBtZXRhPgo8P3hwYWNrZXQgZW5kPSJ3Ij8+Cv/tAJZQaG90b3Nob3AgMy4wADhCSU0EBAAAAAAAehwCUAAMY3VtYWNyZWF0aXZlHAJ4AEdEZWZhdWx0IHByb2ZpbGUgcGljdHVyZSwgYXZhdGFyLCBwaG90byBwbGFjZWhvbGRlci4gVmVjdG9yIGlsbHVzdHJhdGlvbhwCbgAYR2V0dHkgSW1hZ2VzL2lTdG9ja3Bob3Rv/9sAQwAKBwcIBwYKCAgICwoKCw4YEA4NDQ4dFRYRGCMfJSQiHyIhJis3LyYpNCkhIjBBMTQ5Oz4+PiUuRElDPEg3PT47/9sAQwEKCwsODQ4cEBAcOygiKDs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7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zzzzyz//AP8A/wD/AP8A/wD+/wA88888888888888888888888888f8A/wD/AP8A/wD/AP8A/wC/zzzzzzzzzzzzzzzzzzzzzzzzzzzf/wD/AP8A/wD/AP8A/wD/ANPPPPPPPPPPPPPPPPPPPPPPPPPPH/8A/wD/AP8A/wD/AP8A/wDc88888888888888888888888888X/AP8A/wD/AP8A/wD/AP8Atzzzzzzzzzzzzzzzzzzzzzzzzzzzj/8A/wD/AP8A/wD/AP8A7888888888888888888888888888s3//AP8A/wD/AP8A/wDs88888888888888888888888888888/8A/wD/AP8A/wD+tzzzzzzzzzzzzzzzzzzzzzzzzzzzzzxzf/8A/wD/APJzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzwxjDjRzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzxS8/wD/AP8A/wD/AP3/ANzzzzzzzzzzzzzzzzzzzzzzzzy//wD/AP8A/wD/AP8A/wD/AP8A/wDv408888888888888888888k//AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AD8PPPPPPPPPPPPPPPPPJ/8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AL1PPPPPPPPPPPPPPPLP/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wC/Tzzzzzzzzzzzzzhf/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AFPPPPPPPPPPPPPPP/8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wDY88888888888888//AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wA88888888888888/8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD+vzzzzzzzzzzzzzT/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AM8888888888888//wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AO/zzzzzzzzzzzzzz/8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A/wD/AP8A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");
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new EmailTakenException("User with that email already exists");
        }
        var jwtToken = jwtService.generateToken(user);
        userRepository.insert(user);
        final var timeUntilNextRoll =
                user.getNextRandomSpotGeneratedTime() == null
                        ? Duration.ZERO
                        : Duration.between(LocalDateTime.now(), user.getNextRandomSpotGeneratedTime());
        return LoginResponse.builder()
                .accessToken(jwtToken)
                .timeUntilNextRoll(timeUntilNextRoll.getSeconds())
                .currentSpotId(user.getCurrentSpotId())
                .build();
    }

    public LoginResponse login(LoginBody body) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword()));
        // get user
        var user = userRepository.findUserByEmail(body.getEmail());
        if (user.isEmpty() || !passwordEncoder.matches(body.getPassword(), user.get().getPassword()))
            throw new InvalidLoginCredenials("User with that email and password does not exist!");
        var userDto = user.get();
        userDto.setUsername(body.getEmail());
        var jwtToken = jwtService.generateToken(userDto);
        // return jwt token to client
        final var timeUntilNextRoll =
                userDto.getNextRandomSpotGeneratedTime() == null
                        ? Duration.ZERO
                        : Duration.between(LocalDateTime.now(), userDto.getNextRandomSpotGeneratedTime());
        return LoginResponse.builder()
                .accessToken(jwtToken)
                .timeUntilNextRoll(timeUntilNextRoll.getSeconds())
                .currentSpotId(user.get().getCurrentSpotId())
                .build();
    }

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
            final var user =
                    User.builder()
                            .id(userId.get())
                            .username(googleUserDTO.getEmail())
                            .email(googleUserDTO.getEmail())
                            .role(Role.USER)
                            .picture(googleUserDTO.getPicture())
                            .build();
            userRepository.save(user);
            googleUserDTO.setJwtToken(jwtService.generateToken(user));
            googleUserDTO.setTimeUntilNextRoll(
                    Duration.between(LocalDateTime.now(), user.getNextRandomSpotGeneratedTime()));
            userId.incrementAndGet();
            return googleUserDTO;
        } catch (DuplicateKeyException e) {
            throw new UserAlreadyExistsException("User already exists!");
        } catch (Exception e) {
            throw new InvalidAccessTokenException("Invalid access token: " + accessToken);
        }
    }

    public FacebookUserDTO loginWithFacebook(String accessToken) {
        WebClient webClient = WebClient.create();
        try {
            final var facebookUserDTO =
                    webClient
                            .get()
                            .uri("https://graph.facebook.com/v13.0/me?fields=id,name,picture,email")
                            .header("Authorization", "Bearer " + accessToken)
                            .retrieve()
                            .bodyToMono(FacebookUserDTO.class)
                            .block();
            final var user =
                    User.builder()
                            .id(userId.get())
                            .username(facebookUserDTO.getEmail())
                            .email(facebookUserDTO.getEmail())
                            .picture(facebookUserDTO.getPicture().getData().getUrl())
                            .build();
            userRepository.save(user);
            facebookUserDTO.setJwtToken(jwtService.generateToken(user));
            facebookUserDTO.setTimeUntilNextRoll(
                    Duration.between(LocalDateTime.now(), user.getNextRandomSpotGeneratedTime()));
            userId.incrementAndGet();
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
}
