package com.spots.service.spots;

import com.spots.domain.GenericValidator;
import com.spots.domain.Review;
import com.spots.domain.Spot;
import com.spots.domain.User;
import com.spots.dto.ReviewDto;
import com.spots.dto.SpotDto;
import com.spots.repository.SpotsRepository;
import com.spots.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SpotsService {

    @Autowired
    public SpotsRepository spotsRepository;

    @Autowired
    public UserRepository userRepository;

    public void createSpot(SpotDto spotDto){
        Spot spot = Spot.builder()
                .id(randomId())
                .name(spotDto.getName())
                .location(spotDto.getLocation())
                .description(spotDto.getDescription())
                .reviews(new ArrayList<>())
                .conqueredBy(new ArrayList<>())
                .build();
        GenericValidator<Spot> spotValidator = new GenericValidator<>();
        spotValidator.validate(spot);
        if(spotsRepository.existsSpotByName(spot.getName())){
            throw  new InvalidSpotNameException("Spot with this name already exists!");
        }
        spotsRepository.insert(spot);

    }

    public void updateSpot(SpotDto spotDto){
        if(!spotsRepository.existsSpotById(spotDto.getId())){
            throw  new InvalidSpotIdException("Spot with this id doesn't exists!");
        }
        Spot spot = spotsRepository.findById(spotDto.getId()).get();
        fromDtoToEntity(spotDto,spot);
        spotsRepository.save(spot);

    }

    public void deleteSpot(String spotId){
        if(!spotsRepository.existsSpotById(spotId)){
            throw  new InvalidSpotIdException("Spot with this id doesn't exists!");
        }
        spotsRepository.deleteById(spotId);

    }

    public List<Spot> getSpots(){
        return  spotsRepository.findAll();
    }

    //TODO make it pageable list 5 items every page
    public List<Review> getSpotReviews(String spotId){

        List<Review> reviews = spotsRepository.findById(spotId).get().getReviews();

        if(reviews.isEmpty()){
            throw  new InvalidSpotIdException("No reviews posted yet!");
        }

        return  reviews;
    }


    public void addSpotReview(String spotId, ReviewDto reviewDto){
        Review review=Review.builder().build();
        User user =userRepository.findById(reviewDto.getUserId()).get();

        review.setId(randomId());
        review.setComment(reviewDto.getComment());
        review.setRating(reviewDto.getRating());
        review.setUser(user);

        GenericValidator<Review> reviewValidator = new GenericValidator<>();
        reviewValidator.validate(review);
        if(!spotsRepository.existsSpotById(spotId)){
            throw  new InvalidSpotIdException("Spot with this id doesn't exists!");
        }
        Spot spot = spotsRepository.findById(spotId).get();
        spot.getReviews().add(review);
        spotsRepository.save(spot);

    }
    public void updateSpotReview(String spotId,ReviewDto reviewDto){
        if(!spotsRepository.existsSpotById(spotId)){
            throw  new InvalidSpotIdException("Spot with this id doesn't exists!");
        }
        Spot spot = spotsRepository.findById(spotId).get();
        Review existing_review = spot.getReviews().stream()
                .filter(x->x.getId()==reviewDto.getId())
                .findFirst()
                .get();

        if(existing_review==null){
            throw  new InvalidReviewIdException("Review with this id doesn't exists!");
        }
        existing_review.setRating(reviewDto.getRating());
        existing_review.setComment(reviewDto.getComment());

        GenericValidator<Review> reviewValidator = new GenericValidator<>();
        reviewValidator.validate(existing_review);

        spotsRepository.save(spot);

    }

    public void deleteSpotReview(String spotId,String reviewId){
        if(!spotsRepository.existsSpotById(spotId)){
            throw  new InvalidSpotIdException("Spot with this id doesn't exists!");
        }
        Spot spot = spotsRepository.findById(spotId).get();
        Review existing_review =    spot.getReviews().stream()
                .filter(x->x.getId()==reviewId)
                .findFirst()
                .get();

        if(existing_review==null){
            throw  new InvalidReviewIdException("Review with this id doesn't exists!");
        }
        spot.getReviews().removeIf(x->x.getId().equals(reviewId));
        spotsRepository.save(spot);

    }

    public void conquerSpot(String spotId, User user){
        GenericValidator<User> userValidator = new GenericValidator<>();
        userValidator.validate(user);
        Spot spot = spotsRepository.findById(spotId).get();
        if(spot.getConqueredBy().contains(user)){
            throw new SpotConqueredException("Spot is conquered already");
        }
        spot.getConqueredBy().add(user);
        spotsRepository.save(spot);
    }
    private static String randomId() {
        Long max=1000000L;
        Long min =9999999L;
        Random random = new Random();
        return min + random.nextLong() % (max - min + 1)+"";
    }
    public void fromDtoToEntity(SpotDto spotDto,Spot spot) {
        spot.setName(spotDto.getName());
        spot.setLocation(spotDto.getLocation());
        spot.setDescription(spotDto.getDescription());
    }
}
