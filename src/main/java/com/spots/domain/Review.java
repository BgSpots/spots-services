package com.spots.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {
    private User user;

    @Min(1)
    @Max(10)
    private float rating;

    private String comment;
}
