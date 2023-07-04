package com.spots.domain;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Spot {
    private String id;
    private String name;
    private Location location;
    private String description;
    private float rating;
    private List<Review> reviews;
    private List<User> conqueredBy;
}
