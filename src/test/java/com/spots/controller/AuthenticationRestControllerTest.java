package com.spots.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.spots.common.auth.RegisterBody;
import com.spots.config.JwtAuthenticationFilter;
import com.spots.config.SecurityConfiguration;
import com.spots.domain.User;
import com.spots.repository.UserRepository;
import com.spots.service.auth.AuthenticationService;
import com.spots.service.auth.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {AuthenticationRestController.class})
@Import(SecurityConfiguration.class)
class AuthenticationRestControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private UserRepository userRepository;
    @SpyBean private AuthenticationService authenticationService;
    @SpyBean private BCryptPasswordEncoder passwordEncoder;
    @MockBean private AuthenticationManager authenticationManager;
    @SpyBean private JwtAuthenticationFilter jwtAuthFilter;
    @MockBean private RedisTemplate<String, String> redis;
    @MockBean private ValueOperations valueOperations;
    @MockBean private JwtService jwtService;

    static {
        // SHA-256 of "foo"
        System.setProperty(
                "SPOTS_SECRET", "2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae");
    }

    @Test
    void register() throws Exception {
        when(jwtService.generateToken(any())).thenReturn("token");
        final var registerRequest = RegisterBody.builder().email("test1@test.com").password("secret");
        final var result =
                mockMvc
                        .perform(
                                post("/auth/register")
                                        .with(csrf())
                                        .content(new Gson().toJson(registerRequest))
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn();
        assertEquals(
                "{\"access_token\":\"token\",\"time_until_next_roll\":0}",
                result.getResponse().getContentAsString());
    }

    @Test
    void registerInvalidEmail() throws Exception {
        final var registerRequest = RegisterBody.builder().email("test1st.com").password("secret");
        mockMvc
                .perform(
                        post("/auth/register")
                                .with(csrf())
                                .content(new Gson().toJson(registerRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerInvalidPassword() throws Exception {
        final var registerRequest = RegisterBody.builder().email("test@test.com").password("sec");
        mockMvc
                .perform(
                        post("/auth/register")
                                .with(csrf())
                                .content(new Gson().toJson(registerRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login() throws Exception {
        when(jwtService.generateToken(any())).thenReturn("token");
        when(userRepository.findUserByEmail(any()))
                .thenReturn(
                        Optional.of(
                                User.builder()
                                        .password("$2a$10$xfTUr6WPkt3Dh2QSCH2WkOzRomiUsI9zRv7vA8psIZNdVC7uGlSZu")
                                        .build()));
        final var loginRequest = RegisterBody.builder().email("test@test.com").password("secret");
        mockMvc
                .perform(
                        post("/auth/login")
                                .with(csrf())
                                .content(new Gson().toJson(loginRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void loginInvalidEmail() throws Exception {
        when(userRepository.findUserByEmail(any())).thenReturn(Optional.empty());
        final var loginRequest = RegisterBody.builder().email("test@test.com").password("secret");
        mockMvc
                .perform(
                        post("/auth/login")
                                .with(csrf())
                                .content(new Gson().toJson(loginRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginInvalidPassword() throws Exception {
        when(userRepository.findUserByEmail(any()))
                .thenReturn(Optional.of(User.builder().password("wrong").build()));
        final var loginRequest = RegisterBody.builder().email("test@test.com").password("secret");
        mockMvc
                .perform(
                        post("/auth/login")
                                .with(csrf())
                                .content(new Gson().toJson(loginRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logout() {}
}
