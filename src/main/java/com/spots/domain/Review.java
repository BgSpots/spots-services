package com.spots.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
}
