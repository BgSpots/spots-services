package com.spots.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.google.gson.Gson;
import com.spots.common.GenericValidator;
import com.spots.common.input.UserBody;
import com.spots.config.JwtAuthenticationFilter;
import com.spots.config.SecurityConfiguration;
import com.spots.domain.Role;
import com.spots.domain.User;
import com.spots.repository.UserRepository;
import com.spots.service.auth.AuthenticationService;
import com.spots.service.auth.JwtService;
import com.spots.service.user.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = {UserRestController.class})
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
public class UsersSpotControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private UserService userService;

    @MockBean private UserRepository userRepository;
    @SpyBean private AuthenticationService authenticationService;
    @SpyBean private BCryptPasswordEncoder passwordEncoder;
    @MockBean private AuthenticationManager authenticationManager;
    @SpyBean private JwtAuthenticationFilter jwtAuthFilter;
    @MockBean private RedisTemplate<String, String> redis;
    @MockBean private ValueOperations valueOperations;
    @MockBean private JwtService jwtService;

    @MockBean private GenericValidator validator;

    @MockBean private MongoTemplate mongoTemplate;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    static {
        // SHA-256 of "foo"
        System.setProperty(
                "SPOTS_SECRET", "2c26b46b68ffc68ff99b453c1d30413413422d706483bfa0f98a5e886266e7ae");
    }

    private Authentication yourMockAuthentication() {
        return new TestingAuthenticationToken("user", "password", "ROLE_USER");
    }

    @Test
    @WithMockUser
    public void testGetUsers() throws Exception {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(yourMockAuthentication());
        SecurityContextHolder.setContext(securityContext);

        Role role = Role.USER;

        User user1 =
                User.builder()
                        .id(1)
                        .role(role)
                        .username("user1")
                        .email("email123")
                        .password("password1")
                        .build();

        User user2 =
                User.builder()
                        .id(2)
                        .role(role)
                        .username("user2")
                        .email("email1234")
                        .password("password2")
                        .build();

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        when(userService.getUsers()).thenReturn(users);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(user1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value(user1.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(user1.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].password").value(user1.getPassword()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(user2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].username").value(user2.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].email").value(user2.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].password").value(user2.getPassword()));

        verify(userService, times(1)).getUsers();
    }

    @Test
    @WithMockUser
    public void testAddUser() throws Exception {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(yourMockAuthentication());
        SecurityContextHolder.setContext(securityContext);

        UserBody userBody = new UserBody();
        userBody.setId("123");
        userBody.setUsername("usr_test");
        userBody.setPassword("password");
        userBody.setEmail("test_email");

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(new Gson().toJson(userBody)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.action").value("addUser"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User added successfully!"));

        ArgumentCaptor<UserBody> userCaptor = ArgumentCaptor.forClass(UserBody.class);

        verify(userService, times(1)).createUser(userCaptor.capture());

        UserBody userCaptorValue = userCaptor.getValue();

        // Now you can assert that the captured spot has expected values
        assertEquals(userBody.getEmail(), userCaptorValue.getEmail());
        assertEquals(userBody.getUsername(), userCaptorValue.getUsername());
        assertEquals(userBody.getPassword(), userCaptorValue.getPassword());
    }

    @Test
    @WithMockUser
    public void testUpdateUser() throws Exception {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(yourMockAuthentication());
        SecurityContextHolder.setContext(securityContext);

        UserBody userBody = new UserBody();
        userBody.setId("123");
        userBody.setUsername("usr_test");
        userBody.setPassword("password");
        userBody.setEmail("test_email");

        mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(new Gson().toJson(userBody)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.action").value("updateUser"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User updated successfully!"));

        ArgumentCaptor<UserBody> userCaptor = ArgumentCaptor.forClass(UserBody.class);

        verify(userService, times(1)).updateUser(userCaptor.capture());

        UserBody userCaptorValue = userCaptor.getValue();

        // Now you can assert that the captured spot has expected values
        assertEquals(userBody.getEmail(), userCaptorValue.getEmail());
        assertEquals(userBody.getUsername(), userCaptorValue.getUsername());
        assertEquals(userBody.getPassword(), userCaptorValue.getPassword());
    }

    @Test
    @WithMockUser
    public void testDeleteUser() throws Exception {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(yourMockAuthentication());
        SecurityContextHolder.setContext(securityContext);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.delete("/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.action").value("deleteUser"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User deleted successfully!"));

        verify(userService, times(1)).deleteUser(1L);
    }
}
