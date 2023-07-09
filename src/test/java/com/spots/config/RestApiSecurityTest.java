package com.spots.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.spots.common.auth.RegisterBody;
import com.spots.controller.AuthenticationRestController;
import com.spots.repository.UserRepository;
import com.spots.service.auth.AuthenticationService;
import com.spots.service.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {AuthenticationRestController.class})
@Import(SecurityConfiguration.class)
public class RestApiSecurityTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private UserRepository userRepository;
    @MockBean private AuthenticationService authenticationService;
    @SpyBean private JwtAuthenticationFilter jwtAuthFilter;
    @MockBean private RedisTemplate<String, String> redis;
    @MockBean private ValueOperations valueOperations;
    @MockBean private JwtService jwtService;

    @Test
    void testUnauthorizedUserShouldNotBeAbleToLogout() throws Exception {
        mockMvc.perform(post("/auth/logout")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testAuthorizedUserShouldBeAbleToLogout() throws Exception {
        mockMvc.perform(post("/auth/logout")).andExpect(status().isOk());
    }

    @Test
    void testUnauthorizedUser() throws Exception {
        mockMvc.perform(post("/auth/")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void testAuthorizedUser() throws Exception {
        mockMvc.perform(post("/api/nonexisting")).andExpect(status().isNotFound());
    }

    @Test
    void testUnauthorizedUserShouldBeAbleToRegister() throws Exception {
        final var registerRequest = RegisterBody.builder().email("test1@test.com").password("secret");
        mockMvc
                .perform(
                        post("/auth/register")
                                .with(csrf())
                                .content(new Gson().toJson(registerRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testUnauthorizedUserShouldBeAbleToLogin() throws Exception {
        final var registerRequest = RegisterBody.builder().email("test1@test.com").password("secret");
        mockMvc
                .perform(
                        post("/auth/login")
                                .with(csrf())
                                .content(new Gson().toJson(registerRequest))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
