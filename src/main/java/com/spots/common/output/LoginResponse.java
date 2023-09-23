package com.spots.common.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private long timeUntilNextRoll;
    private long currentSpotId;
    private String picture;
    private long id;
}
