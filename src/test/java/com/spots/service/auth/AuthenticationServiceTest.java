package com.spots.service.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.when;

import com.spots.common.auth.LoginBody;
import com.spots.common.auth.RegisterBody;
import com.spots.config.JwtAuthenticationFilter;
import com.spots.domain.User;
import com.spots.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class AuthenticationServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock private AuthenticationManager authenticationManager;

    @MockBean private RedisTemplate<String, String> redis;
    @MockBean private ValueOperations valueOperations;
    private AuthenticationService subject;

    static {
        // SHA-256 of "foo"
        System.setProperty(
                "SPOTS_SECRET", "2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae");
    }

    @BeforeEach
    void setUp() {
        subject =
                new AuthenticationService(
                        userRepository, passwordEncoder, jwtService, authenticationManager, redis);
    }

    @Test
    void registerHappyPath() {
        when(userRepository.existsUserByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("foo");
        when(jwtService.generateToken(any())).thenReturn("fooToken");

        final var result =
                subject.register(RegisterBody.builder().email("test@test.com").password("secret").build());
        assertEquals("fooToken", result.getAccessToken());
    }

    @Test
    void registerWithDuplicateEmail() {
        when(userRepository.existsUserByEmail(any())).thenReturn(true);

        assertThrows(
                EmailTakenException.class,
                () ->
                        subject.register(
                                RegisterBody.builder().email("test@test.com").password("secret").build()));
    }

    @Test
    void registerWithInvalidEmail() {
        final var message =
                assertThrows(
                                InvalidInputException.class,
                                () ->
                                        subject.register(
                                                RegisterBody.builder().email("test").password("secret").build()))
                        .getMessage();
        assertEquals("[Email must be valid!]", message);
    }

    @Test
    void registerWithInvalidPassword() {
        final var message =
                assertThrows(
                                InvalidInputException.class,
                                () ->
                                        subject.register(
                                                RegisterBody.builder().email("test@test.com").password("sec").build()))
                        .getMessage();
        assertEquals("[Password should be between 5 and 15 characters!]", message);
    }

    @Test
    void loginHappyPath() {
        when(userRepository.findUserByEmail(any()))
                .thenReturn(Optional.ofNullable(User.builder().password("secret").build()));
        when(passwordEncoder.encode(any())).thenReturn("secret");
        when(jwtService.generateToken(any())).thenReturn("fooToken");
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        final var result =
                subject.login(LoginBody.builder().email("test@test.com").password("secret").build());
        assertEquals("fooToken", result.getAccessToken());
    }

    @Test
    void loginInvalidEmail() {
        when(userRepository.findUserByEmail(any())).thenReturn(Optional.empty());

        final var message =
                assertThrows(
                                InvalidLoginCredenials.class,
                                () ->
                                        subject.login(
                                                LoginBody.builder().email("test@test.com").password("secret").build()))
                        .getMessage();
        assertEquals("User with that email and password does not exist!", message);
    }

    @Test
    void loginInvalidPassword() {
        when(userRepository.findUserByEmail(any()))
                .thenReturn(Optional.ofNullable(User.builder().password("secret1").build()));
        when(passwordEncoder.matches("secret", "secret1")).thenReturn(false);

        final var message =
                assertThrows(
                                InvalidLoginCredenials.class,
                                () ->
                                        subject.login(
                                                LoginBody.builder().email("test@test.com").password("secret").build()))
                        .getMessage();
        assertEquals("User with that email and password does not exist!", message);
    }
}
