package com.spots.common.output;

import java.time.Duration;
import lombok.Data;

@Data
public class GoogleUserDTO {
    private String id;
    private String email;
    private String name;
    private String picture;
    private String jwtToken;
    private Duration timeUntilNextRoll;
    private long currentSpotId;
}
