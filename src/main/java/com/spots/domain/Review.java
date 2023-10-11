package com.spots.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class Review {
    private long id;
    private Long spotId;
    private UserInfo userInfo;

    @Min(value = 1, message = "Rating must be between 1 and 10.")
    @Max(value = 10, message = "Rating must be between 1 and 10.")
    private float rating;

    private String comment;

    @Transient public static final String SEQUENCE_NAME = "review_sequence";
}
