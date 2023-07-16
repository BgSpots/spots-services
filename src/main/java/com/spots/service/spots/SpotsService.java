package com.spots.service.spots;

import com.spots.domain.Review;
import com.spots.domain.Spot;
import com.spots.domain.User;
import com.spots.dto.SpotDto;
import com.spots.repository.SpotsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpotsService {
    @Autowired
    public SpotsRepository spotsRepository;

    public String createSpot(Spot spot){
        if(!spotsRepository.existsSpotByName(spot.getName())){
            spotsRepository.insert(spot);
            return  "Spot added successfully!";
        }

        return "Spot with this name already exists!";
    }

    public String updateSpot(SpotDto spotDto){

        if(spotsRepository.existsSpotById(spotDto.getId())){
            Spot spot = spotsRepository.findById(spotDto.getId()).get();
            spot.fromDtoToEntity(spotDto);
            spotsRepository.save(spot);
            return  "Spot updated successfully!";
        }

        return "Spot with this id doesn't exists!";

    }

    public String deleteSpot(String spotId){

        if(spotsRepository.existsSpotById(spotId)){
            spotsRepository.deleteById(spotId);
            return  "Spot deleted successfully!";
        }

        return "Spot with this id doesn't exists!";

    }

    public List<Spot> getSpots(){
        return  spotsRepository.findAll();
    }

    public List<Review> getSpotReviews(String spotId){
        return  spotsRepository.findById(spotId).get().getReviews();
    }


    public String addSpotReview(String spotId,Review review){
        if(!spotsRepository.existsSpotById(spotId)){
            Spot spot = spotsRepository.findById(spotId).get();
            spot.getReviews().add(review);
            spotsRepository.save(spot);
            return  "Review added for spot!";
        }

        return "Spot with this id doesn't exists!";

    }
    public String updateSpotReview(String spotId,Review review){

        if(!spotsRepository.existsSpotById(spotId)){
            Spot spot = spotsRepository.findById(spotId).get();
            spot.getReviews().stream()
                    .filter(x->x.getId()==review.getId())
                    .findFirst()
                    .get()
                    .updateReview(review);
            spotsRepository.save(spot);
            return  "Review updated for spot!";
        }

        return "Spot with this id doesn't exists!";

    }

    public String deleteSpotReview(String spotId,String reviewId){
        if(!spotsRepository.existsSpotById(spotId)){
            Spot spot = spotsRepository.findById(spotId).get();
            spot.getReviews().removeIf(x->x.getId().equals(reviewId));
            spotsRepository.save(spot);
            return  "Review deleted from spot!";
        }

        return "Spot with this id doesn't exists!";


    }


    public String conquerSpot(String spotId, User user){
        Spot spot = spotsRepository.findById(spotId).get();
        if(spot.getConqueredBy().contains(user)){
            return  "Already conquered!";
        }
        spot.getConqueredBy().add(user);
        spotsRepository.save(spot);
        return  "Conquering confirmed!";
    }

}
