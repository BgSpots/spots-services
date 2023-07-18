package com.spots.service.spots;

import com.spots.common.GenericValidator;
import com.spots.domain.Review;
import com.spots.domain.Spot;
import com.spots.domain.User;
import com.spots.dto.ReviewDto;
import com.spots.dto.SpotDto;
import com.spots.repository.SpotsRepository;
import com.spots.repository.UserRepository;
import com.spots.service.user.InvalidUserException;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpotsService {

    public static final String SPOT_WITH_THIS_ID_DOESN_T_EXISTS = "Spot with this id doesn't exists!";
    public static final String USER_WITH_THIS_ID_DOESN_T_EXISTS = "User with this id doesn't exists!";
    private final SpotsRepository spotsRepository;
    private final GenericValidator<Spot> spotValidator;
    private final UserRepository userRepository;
    private static final Random random = new Random();

    public void createSpot(SpotDto spotDto) {
        Spot spot =
                Spot.builder()
                        .id(randomId())
                        .name(spotDto.getName())
                        .location(spotDto.getLocation())
                        .description(spotDto.getDescription())
                        .reviews(new ArrayList<>())
                        .conqueredBy(new ArrayList<>())
                        .build();
        spotValidator.validate(spot);
        if (spotsRepository.existsSpotByName(spot.getName())) {
            throw new InvalidSpotNameException("Spot with this name already exists!");
        }
        spotsRepository.insert(spot);
    }

    public void updateSpot(SpotDto spotDto) {
        Spot spot =
                spotsRepository
                        .findById(spotDto.getId())
                        .orElseThrow(() -> new InvalidSpotIdException(SPOT_WITH_THIS_ID_DOESN_T_EXISTS));
        fromDtoToEntity(spotDto, spot);
        spotsRepository.save(spot);
    }

    public void deleteSpot(String spotId) {
        if (!spotsRepository.existsSpotById(spotId)) {
            throw new InvalidSpotIdException(SPOT_WITH_THIS_ID_DOESN_T_EXISTS);
        }
        spotsRepository.deleteById(spotId);
    }

    public List<Spot> getSpots() {
        return spotsRepository.findAll();
    }

    // TODO make it pageable list 5 items every page
    public List<Review> getSpotReviews(String spotId) {
        Spot spot =
                spotsRepository
                        .findById(spotId)
                        .orElseThrow(() -> new InvalidSpotIdException(SPOT_WITH_THIS_ID_DOESN_T_EXISTS));
        List<Review> reviews = spot.getReviews();

        if (reviews.isEmpty()) {
            throw new InvalidSpotIdException("No reviews posted yet!");
        }

        return reviews;
    }

    public void addSpotReview(String spotId, ReviewDto reviewDto) {
        Review review = Review.builder().build();
        User user =
                userRepository
                        .findById(reviewDto.getUserId())
                        .orElseThrow(() -> new InvalidUserException(USER_WITH_THIS_ID_DOESN_T_EXISTS));

        review.setId(randomId());
        review.setComment(reviewDto.getComment());
        review.setRating(reviewDto.getRating());
        review.setUser(user);

        GenericValidator<Review> reviewValidator = new GenericValidator<>();
        reviewValidator.validate(review);
        Spot spot =
                spotsRepository
                        .findById(spotId)
                        .orElseThrow(() -> new InvalidSpotIdException(SPOT_WITH_THIS_ID_DOESN_T_EXISTS));
        spot.getReviews().add(review);
        spotsRepository.save(spot);
    }

    public void updateSpotReview(String spotId, ReviewDto reviewDto) {
        Spot spot =
                spotsRepository
                        .findById(spotId)
                        .orElseThrow(() -> new InvalidSpotIdException(SPOT_WITH_THIS_ID_DOESN_T_EXISTS));
        Review existingReview =
                spot.getReviews().stream()
                        .filter(x -> x.getId() == reviewDto.getId())
                        .findFirst()
                        .orElseThrow(() -> new InvalidReviewIdException("Review with this id doesn't exists!"));
        existingReview.setRating(reviewDto.getRating());
        existingReview.setComment(reviewDto.getComment());

        GenericValidator<Review> reviewValidator = new GenericValidator<>();
        reviewValidator.validate(existingReview);

        spotsRepository.save(spot);
    }

    public void deleteSpotReview(String spotId, String reviewId) {
        if (!spotsRepository.existsSpotById(spotId)) {
            throw new InvalidSpotIdException(SPOT_WITH_THIS_ID_DOESN_T_EXISTS);
        }
        Spot spot =
                spotsRepository
                        .findById(spotId)
                        .orElseThrow(() -> new InvalidSpotIdException(SPOT_WITH_THIS_ID_DOESN_T_EXISTS));
        spot.getReviews().stream()
                .filter(x -> x.getId() == reviewId)
                .findFirst()
                .orElseThrow(() -> new InvalidReviewIdException("Review with this id doesn't exists!"));
        spot.getReviews().removeIf(x -> x.getId().equals(reviewId));
        spotsRepository.save(spot);
    }

    public void conquerSpot(String spotId, User user) {
        GenericValidator<User> userValidator = new GenericValidator<>();
        userValidator.validate(user);
        Spot spot =
                spotsRepository
                        .findById(spotId)
                        .orElseThrow(() -> new InvalidSpotIdException(SPOT_WITH_THIS_ID_DOESN_T_EXISTS));
        if (spot.getConqueredBy().contains(user)) {
            throw new SpotConqueredException("Spot is conquered already");
        }
        spot.getConqueredBy().add(user);
        spotsRepository.save(spot);
    }

    private static String randomId() {
        long max = 1000000L;
        long min = 9999999L;
        return min + random.nextLong() % (max - min + 1) + "";
    }

    public void fromDtoToEntity(SpotDto spotDto, Spot spot) {
        spot.setName(spotDto.getName());
        spot.setLocation(spotDto.getLocation());
        spot.setDescription(spotDto.getDescription());
        spotValidator.validate(spot);
    }
}