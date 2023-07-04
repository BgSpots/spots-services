package com.spots.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {
    private String id;
    private User user;
    private float rating;
    private String comment;
}
