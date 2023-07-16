package com.spots.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class Review {
    private String id;
    private User user;


    @Min(1)
    @Max(10)
    private float rating;

    private String comment;

    public void  updateReview(Review newReview) {
        this.rating = newReview.getRating();
        this.comment = newReview.getComment();
    }
}
