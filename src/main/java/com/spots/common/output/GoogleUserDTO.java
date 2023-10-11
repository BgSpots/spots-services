package com.spots.common.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import lombok.Data;

@Data
public class GoogleUserDTO {

    private long id;
    private String email;
    private boolean verifiedEmail;
    private String name;
    private String picture;
    private String jwtToken;
    private Duration timeUntilNextRoll;

    @JsonCreator
    public GoogleUserDTO(
            @JsonProperty("email") String email,
            @JsonProperty("verifiedEmail") boolean verifiedEmail,
            @JsonProperty("name") String name,
            @JsonProperty("picture") String picture) {

        this.email = email;
        this.verifiedEmail = verifiedEmail;
        this.name = name;
        this.picture = picture;
    }
}
