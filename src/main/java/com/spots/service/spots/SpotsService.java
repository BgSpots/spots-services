package com.spots.service.spots;

import com.spots.common.GenericValidator;
import com.spots.common.input.ReviewBody;
import com.spots.common.input.SpotDto;
import com.spots.config.InvalidJwtTokenException;
import com.spots.domain.*;
import com.spots.repository.PaymentRepository;
import com.spots.repository.ReviewRepository;
import com.spots.repository.SpotsRepository;
import com.spots.repository.UserRepository;
import com.spots.service.auth.JwtService;
import com.spots.service.user.InvalidUserException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
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
    private final GenericValidator<Spot> spotValidator = new GenericValidator<>();
    private final GenericValidator<Review> reviewValidator = new GenericValidator<>();
    private final UserRepository userRepository;
    private static final Random random = new Random();
    private final JwtService jwtService;
    private final ReviewRepository reviewRepository;
    private final PaymentRepository paymentRepository;
    public static AtomicLong spotId = new AtomicLong(1L);
    public static AtomicLong reviewId = new AtomicLong(1L);

    public void createSpot(SpotDto spotDto) {
        Location location =
                Location.builder()
                        .latitude(spotDto.getLocation().getLatitude())
                        .longitude(spotDto.getLocation().getLongitude())
                        .build();
        Spot spot =
                Spot.builder()
                        .id(spotId.get())
                        .name(spotDto.getName())
                        .location(location)
                        .overallRating(1)
                        .description(spotDto.getDescription())
                        .imageBase64(spotDto.getImageBase64())
                        .build();
        spotValidator.validate(spot);
        if (spotsRepository.existsSpotByName(spot.getName())) {
            throw new InvalidSpotNameException("Spot with this name already exists!");
        }
        spotsRepository.insert(spot);
        spotId.incrementAndGet();
    }

    public void updateSpot(SpotDto spotDto) {
        Spot spot =
                spotsRepository
                        .findById(spotDto.getId())
                        .orElseThrow(() -> new InvalidSpotIdException(SPOT_WITH_THIS_ID_DOESN_T_EXISTS));
        spot.setDescription(spotDto.getDescription());
        spotsRepository.save(spot);
    }

    public void deleteSpot(Long spotId) {
        if (!spotsRepository.existsSpotById(spotId)) {
            throw new InvalidSpotIdException(SPOT_WITH_THIS_ID_DOESN_T_EXISTS);
        }
        spotsRepository.deleteById(spotId);
    }

    public List<Spot> getSpots() {
        return spotsRepository.findAll();
    }

    public Spot getSpot(Long id) {
        return spotsRepository
                .findById(id)
                .orElseThrow(() -> new InvalidSpotIdException("Invalid spot id"));
    }

    public Spot getRandomSpot(String authHeader) {
        if (spotsRepository.count() == 0) throw new InvalidSpotIdException("No spots available yet");
        Long randomIndex = random.nextLong(spotsRepository.count()) + 1;
        String jwt = authHeader.substring(7);
        final var user =
                userRepository
                        .findUserByEmail(jwtService.extractEmail(jwt))
                        .orElseThrow(() -> new InvalidJwtTokenException("Invalid jwt token"));
        final var payment = paymentRepository.findPaymentByUserId(user.getId()).orElse(null);
        final var isPayed =
                payment != null && ("paid".equals(payment.getStatus()) || payment.isAdWatched());
        if (!isPayed) {
            if (user.getNextRandomSpotGeneratedTime() != null
                    && LocalDateTime.now().isBefore(user.getNextRandomSpotGeneratedTime())) {
                throw new RandomSpotIsNotAvailableYet("Random spot is not available yet!");
            }
        } else {
            if (payment.isUsed()) throw new SpotRerollAlreadyUsed("Spot reroll already used!");
        }
        while (true) {
            final var randomSpot = spotsRepository.findById(randomIndex);
            if (randomSpot.isPresent()) {
                user.setNextRandomSpotGeneratedTime(LocalDateTime.now().plus(Duration.ofDays(7)));
                user.setCurrentSpotId(randomIndex);
                if (isPayed) {
                    payment.setUsed(true);
                    paymentRepository.save(payment);
                }
                userRepository.save(user);
                return randomSpot.get();
            }
            randomIndex = random.nextLong(spotsRepository.count());
        }
    }

    public List<Review> getSpotReviews(Long spotId, Integer pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 5);
        List<Review> reviews = reviewRepository.findAllBySpotId(spotId, pageable).getContent();

        if (reviews.isEmpty()) {
            throw new InvalidSpotIdException("No reviews posted yet!");
        }

        return reviews;
    }

    public void addSpotReview(Long spotId, ReviewBody reviewBody, String authHeader) {
        final String jwt = authHeader.substring(7);
        User user =
                userRepository
                        .findUserByEmail(jwtService.extractEmail(jwt))
                        .orElseThrow(() -> new InvalidUserException(USER_WITH_THIS_ID_DOESN_T_EXISTS));
        Review review =
                Review.builder()
                        .id(reviewId.get())
                        .spotId(spotId)
                        .comment(reviewBody.getComment())
                        .rating(reviewBody.getRating())
                        .build();
        UserInfo reviewerInfo =
                UserInfo.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .profilePicture(user.getPicture())
                        .build();
        review.setUserInfo(reviewerInfo);

        reviewValidator.validate(review);
        Spot spot =
                spotsRepository
                        .findById(spotId)
                        .orElseThrow(() -> new InvalidSpotIdException(SPOT_WITH_THIS_ID_DOESN_T_EXISTS));
        List<Review> reviews =
                reviewRepository.findAllBySpotId(spotId, Pageable.ofSize(10)).getContent();
        OptionalDouble averageRating = reviews.stream().mapToDouble(Review::getRating).average();
        double overallRating = averageRating.orElse(0.0); // Default to 0 if there are no reviews
        spot.setOverallRating((float) overallRating);

        reviewRepository.insert(review);
        spotsRepository.save(spot);
        reviewId.incrementAndGet();
    }

    public void deleteSpotReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new InvalidSpotIdException(SPOT_WITH_THIS_ID_DOESN_T_EXISTS);
        }
        reviewRepository.deleteById(reviewId);
    }
}
