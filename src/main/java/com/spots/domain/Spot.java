package com.spots.domain;

import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class Spot {
    private String id;
    private String name;
    private Location location;
    private String description;

    @Min(1)
    @Max(10)
    private float overallRating;

    private List<Review> reviews;
    private List<User> conqueredBy;
}
