package com.spots.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class Spot {
    private String id;
    @NotBlank private String name;
    private Location location;
    @NotBlank private String description;

    @Min(value = 1, message = "Overall rating must be between 1 and 10.")
    @Max(value = 10, message = "Overall rating must be between 1 and 10.")
    private float overallRating;
}
