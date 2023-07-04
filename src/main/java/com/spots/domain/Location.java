package com.spots.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location {
    private float latitude;
    private float longitude;
}
