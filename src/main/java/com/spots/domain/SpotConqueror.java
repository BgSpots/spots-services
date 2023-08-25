package com.spots.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class SpotConqueror {
    private String id;
    private Long spotId;
    private String username;
    private String profilePicture;
}
