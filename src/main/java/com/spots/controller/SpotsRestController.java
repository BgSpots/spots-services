package com.spots.controller;

import com.spots.domain.Review;
import com.spots.domain.Spot;
import com.spots.domain.User;
import com.spots.dto.SpotDto;
import com.spots.service.auth.EmailTakenException;
import com.spots.service.auth.InvalidInputException;
import com.spots.service.spots.SpotsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@RestController
@RequestMapping("/spots")
@SecurityRequirement(name = "Bearer Authentication")
class SpotsRestController {

    @Autowired
    public SpotsService spotsService;

    @GetMapping
    @Operation(summary = "Get all existing spots", description = "Returns a list of spots entity.")
    public ResponseEntity<?>  getSpots(HttpServletRequest request){
        try {
            List<Spot> spots =spotsService.getSpots();
            return ResponseEntity.ok(spots);
        } catch (EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }
    @PostMapping("/spot")
    @Operation(summary = "Adds a new spot", description = "Adds new spot entity.")
    public ResponseEntity<?>  addSpot(@RequestBody SpotDto spotDto, HttpServletRequest request){
        try {
            spotDto.getLocation().setId(randomId());
            Spot spot = Spot.builder()
                    .id(randomId())
                    .name(spotDto.getName())
                    .location(spotDto.getLocation())
                    .description(spotDto.getDescription())
                    .reviews(new ArrayList<>())
                    .conqueredBy(new ArrayList<>())
                    .build();


            ApiSuccess successResponse=new ApiSuccess("addSpot",spotsService.createSpot(spot));
            return ResponseEntity.ok(successResponse);
        } catch (EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/spot")
    @Operation(summary = "Updates spot information", description = "Updates spot entity information.")
    public ResponseEntity<?>  updateSpot(@RequestBody SpotDto spotDto,HttpServletRequest request){
        try {

            ApiSuccess successResponse=new ApiSuccess("updateSpot",spotsService.updateSpot(spotDto));
            return ResponseEntity.ok(successResponse);
        } catch (EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }
    @DeleteMapping("/spot/{spotId}")
    @Operation(summary = "Deletes spot", description = "Deletes specific spot by id.")
    public ResponseEntity<?>  deleteSpot(@PathVariable String spotId,HttpServletRequest request){
        try {
            ApiSuccess successResponse=new ApiSuccess("deleteSpot",spotsService.deleteSpot(spotId));
            return ResponseEntity.ok(successResponse);
        } catch (EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }
    //TODO pageable reviews from (example 1 - 5)
    @GetMapping("/spot/{spotId}/reviews")
    @Operation(summary = "Get all reviews from spot", description = "Returns a list of review")
    public ResponseEntity<?>  getSpotReviews(@PathVariable String spotId, HttpServletRequest request){
        try {

            List<Review> reviews =spotsService.getSpotReviews(spotId);
            if(reviews.isEmpty()){
                ApiSuccess successResponse=new ApiSuccess("getReviews","No reviews posted yet!");
                return ResponseEntity.ok(successResponse);
            }
            return  ResponseEntity.ok(reviews);

        } catch (EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }
    @PostMapping("/spot/{spotId}/reviews/review")
    @Operation(summary = "Adds new review to spot", description = "Adds new review to specific spot using spots id.")
    public ResponseEntity<?>  addSpotReview(@PathVariable String spotId,@RequestBody Review review, HttpServletRequest request){
        try {
            ApiSuccess successResponse=new ApiSuccess("addReview",spotsService.addSpotReview(spotId,review));
            return ResponseEntity.ok(successResponse);
        } catch (EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }
    @PutMapping("/spot/{spotId}/reviews/review")
    @Operation(summary = "Updates existing review from spot", description = "Updates review from specific spot using spots id.")
    public ResponseEntity<?>  updateSpotReview(@PathVariable String spotId,@RequestBody Review review, HttpServletRequest request){
        try {

            ApiSuccess successResponse=new ApiSuccess("updateReview",spotsService.updateSpotReview(spotId,review));
            return ResponseEntity.ok(successResponse);
        } catch (EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }
    @DeleteMapping("/spot/{spotId}/reviews/review")
    @Operation(summary = "Deletes review from spot", description = "Deletes review from specific spot using spots id and the review id.")
    public ResponseEntity<?>  deleteSpotReview(@PathVariable String spotId,@RequestParam String reviewId, HttpServletRequest request){
        try {
            ApiSuccess successResponse=new ApiSuccess("deleteReview",spotsService.deleteSpotReview(spotId,reviewId));
            return ResponseEntity.ok(successResponse);
        } catch (EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }
    @PostMapping("/spot/{spotId}/conquer")
    @Operation(summary = " Adds user who have visited this spot", description = "Adds user entity to the spots conquered list.")
    public ResponseEntity<?> conquerSpot(@PathVariable String spotId, @RequestBody User user, HttpServletRequest request){
        try {
            ApiSuccess successResponse=new ApiSuccess("conquerSpot",spotsService.conquerSpot(spotId,user));
            return ResponseEntity.ok(successResponse);
        } catch (EmailTakenException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }
    private static String randomId() {
        Long max=1000000L;
        Long min =9999999L;
        Random random = new Random();
        return min + random.nextLong() % (max - min + 1)+"";
    }
}
