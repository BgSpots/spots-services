package com.spots.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpotDetails {
    private Spot spot;

    private String additionalInfo;
}
