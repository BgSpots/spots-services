package com.spots.domain;

import com.spots.dto.SpotDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class Spot {
    private String id;
    private String name;
    private Location location;
    private String description;


    public void fromDtoToEntity(SpotDto spotDto) {
        this.name=spotDto.getName();
        this.location=spotDto.getLocation();
        this.description=spotDto.getDescription();
    }

    @Min(1)
    @Max(10)
    private float overallRating;

    private List<Review> reviews;
    private List<User> conqueredBy;
}
