package com.spots.service.spots;

import com.spots.common.GenericValidator;
import com.spots.domain.*;
import com.spots.dto.ReviewDto;
import com.spots.dto.SpotDto;
import com.spots.dto.UserDto;
import com.spots.repository.ReviewRepository;
import com.spots.repository.SpotConquerorRepository;
import com.spots.repository.SpotsRepository;
import com.spots.repository.UserRepository;
import com.spots.service.user.InvalidUserException;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpotsService {

    public static final String SPOT_WITH_THIS_ID_DOESN_T_EXISTS = "Spot with this id doesn't exists!";
    public static final String USER_WITH_THIS_ID_DOESN_T_EXISTS = "User with this id doesn't exists!";
    private final SpotsRepository spotsRepository;
    private final GenericValidator<Spot> spotValidator;
    private final UserRepository userRepository;
    private final SpotConquerorRepository spotConquerorRepository;
    private static final Random random = new Random();

    private final ReviewRepository reviewRepository;

    public void createSpot(SpotDto spotDto) {
        Spot spot =
                Spot.builder()
                        .id(randomId())
                        .name(spotDto.getName())
                        .location(spotDto.getLocation())
                        .overallRating(1)
                        .description(spotDto.getDescription())
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

    public List<Review> getSpotReviews(String spotId, Integer pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 5);
        List<Review> reviews = reviewRepository.findAllBySpotId(spotId, pageable).getContent();

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
        review.setSpotId(spotId);
        review.setComment(reviewDto.getComment());
        review.setRating(reviewDto.getRating());
        UserInfo reviewerInfo = new UserInfo();
        reviewerInfo.setUserId(user.getId());
        reviewerInfo.setUsername(user.getUsername());
        reviewerInfo.setProfilePicture(user.getPicture());
        review.setUserInfo(reviewerInfo);

        GenericValidator<Review> reviewValidator = new GenericValidator<>();
        reviewValidator.validate(review);
        Spot spot =
                spotsRepository
                        .findById(spotId)
                        .orElseThrow(() -> new InvalidSpotIdException(SPOT_WITH_THIS_ID_DOESN_T_EXISTS));
        reviewRepository.insert(review);

        spotsRepository.save(spot);
    }

    public void updateSpotReview(ReviewDto reviewDto) {
        Review existingReview =
                reviewRepository
                        .findById(reviewDto.getId())
                        .orElseThrow(() -> new InvalidReviewIdException("Review with this id doesn't exists!"));
        existingReview.setRating(reviewDto.getRating());
        existingReview.setComment(reviewDto.getComment());

        GenericValidator<Review> reviewValidator = new GenericValidator<>();
        reviewValidator.validate(existingReview);

        reviewRepository.save(existingReview);
    }

    public void deleteSpotReview(String reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new InvalidSpotIdException(SPOT_WITH_THIS_ID_DOESN_T_EXISTS);
        }
        reviewRepository.deleteById(reviewId);
    }

    public List<SpotConqueror> getConquerorsOfSpot(String spotId, Integer pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 5);
        List<SpotConqueror> conquerors =
                spotConquerorRepository.findAllBySpotId(spotId, pageable).getContent();
        if (conquerors.isEmpty()) {
            throw new InvalidSpotIdException("No one has conquered this spot yet!");
        }
        return conquerors;
    }

    public void conquerSpot(String spotId, UserDto userDto) {
        if (userRepository.findById(userDto.getId()).isEmpty()) {
            throw new SpotConqueredException("User doesn't exist");
        }

        if (!spotConquerorRepository.findSpotConquerorByUsername(userDto.getUsername()).isEmpty()) {
            throw new SpotConqueredException("Spot is conquered already");
        }

        SpotConqueror spotConqueror =
                SpotConqueror.builder()
                        .username(userDto.getUsername())
                        .spotId(spotId)
                        .profilePicture(userDto.getPicture())
                        .build();

        spotConquerorRepository.save(spotConqueror);
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
