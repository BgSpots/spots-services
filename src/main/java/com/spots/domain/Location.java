package com.spots.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class Location {
    private String id;
    private float latitude;
    private float longitude;
}
