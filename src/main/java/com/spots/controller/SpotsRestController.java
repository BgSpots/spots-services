package com.spots.controller;

import com.spots.domain.Spot;
import com.spots.domain.User;
import com.spots.dto.ReviewDto;
import com.spots.dto.SpotDto;
import com.spots.service.auth.InvalidInputException;
import com.spots.service.spots.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spots")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
class SpotsRestController {

    private final SpotsService spotsService;

    @GetMapping
    @Operation(summary = "Get all existing spots", description = "Returns a list of spots entity.")
    public ResponseEntity<?> getSpots(HttpServletRequest request) {
        List<Spot> spots = spotsService.getSpots();
        return ResponseEntity.ok(spots);
    }

    @PostMapping
    @Operation(summary = "Adds a new spot", description = "Adds new spot entity.")
    public ResponseEntity<?> addSpot(@RequestBody SpotDto spotDto, HttpServletRequest request) {
        try {
            spotsService.createSpot(spotDto);
            ApiSuccess successResponse = new ApiSuccess("addSpot", "Spot added successfully!");
            return ResponseEntity.ok(successResponse);
        } catch (InvalidSpotNameException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping
    @Operation(summary = "Updates spot information", description = "Updates spot entity information.")
    public ResponseEntity<?> updateSpot(@RequestBody SpotDto spotDto, HttpServletRequest request) {
        try {
            spotsService.updateSpot(spotDto);
            ApiSuccess successResponse = new ApiSuccess("updateSpot", "Spot updated successfully!");
            return ResponseEntity.ok(successResponse);
        } catch (InvalidSpotIdException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{spotId}")
    @Operation(summary = "Deletes spot", description = "Deletes specific spot by id.")
    public ResponseEntity<?> deleteSpot(@PathVariable String spotId, HttpServletRequest request) {
        try {
            spotsService.deleteSpot(spotId);
            ApiSuccess successResponse = new ApiSuccess("deleteSpot", "Spot deleted successfully!");
            return ResponseEntity.ok(successResponse);
        } catch (InvalidSpotIdException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{spotId}/reviews")
    @Operation(summary = "Get all reviews from spot", description = "Returns a list of review")
    public ResponseEntity<?> getSpotReviews(
            @PathVariable String spotId, @RequestParam Integer pageNum, HttpServletRequest request) {
        try {
            return ResponseEntity.ok(spotsService.getSpotReviews(spotId, pageNum));

        } catch (NoReviewsException | InvalidSpotIdException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{spotId}/reviews")
    @Operation(
            summary = "Adds new review to spot",
            description = "Adds new review to specific spot using spots id.")
    public ResponseEntity<?> addSpotReview(
            @PathVariable String spotId, @RequestBody ReviewDto review, HttpServletRequest request) {
        try {
            spotsService.addSpotReview(spotId, review);
            ApiSuccess successResponse = new ApiSuccess("addReview", "Review added for spot!");
            return ResponseEntity.ok(successResponse);
        } catch (InvalidSpotIdException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{spotId}/reviews")
    @Operation(
            summary = "Updates existing review from spot",
            description = "Updates review from specific spot using spots id.")
    public ResponseEntity<?> updateSpotReview(
            @PathVariable String spotId, @RequestBody ReviewDto review, HttpServletRequest request) {
        try {
            spotsService.updateSpotReview(spotId, review);
            ApiSuccess successResponse = new ApiSuccess("updateReview", "Review updated for spot!");
            return ResponseEntity.ok(successResponse);
        } catch (InvalidReviewIdException | InvalidSpotIdException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{spotId}/reviews")
    @Operation(
            summary = "Deletes review from spot",
            description = "Deletes review from specific spot using spots id and the review id.")
    public ResponseEntity<?> deleteSpotReview(
            @PathVariable String spotId, @RequestParam String reviewId, HttpServletRequest request) {
        try {
            spotsService.deleteSpotReview(spotId, reviewId);
            ApiSuccess successResponse = new ApiSuccess("deleteReview", "Review deleted from spot!");
            return ResponseEntity.ok(successResponse);
        } catch (InvalidReviewIdException | InvalidSpotIdException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{spotId}/conquer")
    @Operation(
            summary = " Adds user who have visited this spot",
            description = "Adds user entity to the spots conquered list.")
    public ResponseEntity<?> conquerSpot(
            @PathVariable String spotId, @RequestBody User user, HttpServletRequest request) {
        try {
            spotsService.conquerSpot(spotId, user);
            ApiSuccess successResponse = new ApiSuccess("conquerSpot", "Already conquered!");
            return ResponseEntity.ok(successResponse);
        } catch (SpotConqueredException | InvalidInputException e) {
            ApiError error =
                    new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage(), request.getRequestURI());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
