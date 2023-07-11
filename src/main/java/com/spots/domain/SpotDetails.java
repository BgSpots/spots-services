package com.spots.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class SpotDetails {
    private String id;
    private Spot spot;
    private String additionalInfo;
}
