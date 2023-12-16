package com.spots.common.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpotDto {
    private Long id;
    private String name;
    private LocationBody location;
    private String description;
    private String imageName;
}
