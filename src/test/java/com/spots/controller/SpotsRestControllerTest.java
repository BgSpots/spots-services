package com.spots.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.spots.common.GenericValidator;
import com.spots.common.input.LocationBody;
import com.spots.common.input.ReviewBody;
import com.spots.common.input.SpotDto;
import com.spots.config.JwtAuthenticationFilter;
import com.spots.config.SecurityConfiguration;
import com.spots.domain.Review;
import com.spots.domain.Spot;
import com.spots.repository.SpotsRepository;
import com.spots.repository.UserRepository;
import com.spots.service.auth.AuthenticationService;
import com.spots.service.auth.JwtService;
import com.spots.service.spots.SpotsService;
import java.util.ArrayList;
import java.util.Arrays;
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

@WebMvcTest(controllers = {SpotsRestController.class})
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
public class SpotsRestControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private SpotsService spotsService;

    @MockBean private SpotsRepository spotsRepository;
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
    public void testGetSpots() throws Exception {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(yourMockAuthentication());
        SecurityContextHolder.setContext(securityContext);

        Spot spot1 =
                Spot.builder().id(123L).name("spot1").description("description1").overallRating(1).build();

        Spot spot2 =
                Spot.builder().id(456L).name("spot2").description("description2").overallRating(2).build();

        List<Spot> spots = Arrays.asList(spot1, spot2);

        when(spotsService.getSpots()).thenReturn(spots);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/spots")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(spot1.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(spot1.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(spot1.getDescription()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$[0].overallRating").value(spot1.getOverallRating()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value(spot2.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value(spot2.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value(spot2.getDescription()))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$[1].overallRating").value(spot2.getOverallRating()));

        verify(spotsService, times(1)).getSpots();
    }

    @Test
    @WithMockUser
    public void testAddSpot() throws Exception {
        SpotDto spot = new SpotDto();
        spot.setName("test123");
        spot.setDescription("description123");

        LocationBody location = LocationBody.builder().latitude(2).longitude(3).build();
        spot.setLocation(location);

        mockMvc
                .perform(
                        post("/spots")
                                .with(csrf())
                                .content(new Gson().toJson(spot))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.action").value("addSpot"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Spot added successfully!"));

        ArgumentCaptor<SpotDto> spotCaptor = ArgumentCaptor.forClass(SpotDto.class);
        verify(spotsService, times(1)).createSpot(spotCaptor.capture());

        SpotDto capturedSpot = spotCaptor.getValue();

        // Now you can assert that the captured spot has expected values
        assertEquals("test123", capturedSpot.getName());
        assertEquals("description123", capturedSpot.getDescription());
        assertEquals(2, capturedSpot.getLocation().getLatitude());
        assertEquals(3, capturedSpot.getLocation().getLongitude());
    }

    @Test
    @WithMockUser
    public void testUpdateSpot() throws Exception {
        SpotDto spot = new SpotDto();
        spot.setName("test123");
        spot.setDescription("description123");

        LocationBody location = LocationBody.builder().latitude(2).longitude(3).build();
        spot.setLocation(location);

        mockMvc
                .perform(
                        put("/spots")
                                .with(csrf())
                                .content(new Gson().toJson(spot))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.action").value("updateSpot"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Spot updated successfully!"));

        ArgumentCaptor<SpotDto> spotCaptor = ArgumentCaptor.forClass(SpotDto.class);
        verify(spotsService, times(1)).updateSpot(spotCaptor.capture());

        SpotDto capturedSpot = spotCaptor.getValue();

        // Now you can assert that the captured spot has expected values
        assertEquals("test123", capturedSpot.getName());
        assertEquals("description123", capturedSpot.getDescription());
        assertEquals(2, capturedSpot.getLocation().getLatitude());
        assertEquals(3, capturedSpot.getLocation().getLongitude());
    }

    @Test
    @WithMockUser
    public void testDeleteSpot() throws Exception {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(yourMockAuthentication());
        SecurityContextHolder.setContext(securityContext);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.delete("/spots/123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.action").value("deleteSpot"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Spot deleted successfully!"));
        ;

        verify(spotsService, times(1)).deleteSpot(123L);
    }

    @Test
    @WithMockUser
    public void testGetSpotReviews() throws Exception {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(yourMockAuthentication());
        SecurityContextHolder.setContext(securityContext);

        Review review = Review.builder().rating(1).comment("comment").build();

        List<Review> reviews = new ArrayList<>();
        reviews.add(review);

        when(spotsService.getSpotReviews(123L, 1)).thenReturn(reviews);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/spots/123/reviews?pageNum=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].rating").value(review.getRating()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].comment").value(review.getComment()));

        verify(spotsService, times(1)).getSpotReviews(123L, 1);
    }

    @Test
    @WithMockUser
    public void testAddSpotReview() throws Exception {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(yourMockAuthentication());
        SecurityContextHolder.setContext(securityContext);

        ReviewBody review = new ReviewBody(2, "comment");

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/spots/123/reviews")
                                .content(new Gson().toJson(review))
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.action").value("addReview"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Review added for spot!"));
        ;

        ArgumentCaptor<ReviewBody> reviewCaptor = ArgumentCaptor.forClass(ReviewBody.class);
        verify(spotsService, times(1))
                .addSpotReview(anyLong(), reviewCaptor.capture(), nullable(String.class));

        ReviewBody reviewBody = reviewCaptor.getValue();

        // Now you can assert that the captured spot has expected values
        assertEquals(review.getRating(), reviewBody.getRating());
        assertEquals(review.getComment(), reviewBody.getComment());
    }

    @Test
    @WithMockUser
    public void testDeleteSpotReview() throws Exception {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(yourMockAuthentication());
        SecurityContextHolder.setContext(securityContext);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.delete("/spots/reviews?reviewId=1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.action").value("deleteReview"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Review deleted from spot!"));
        ;

        verify(spotsService, times(1)).deleteSpotReview(1L);
    }
}
