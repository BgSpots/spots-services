package com.spots.service.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoogleUserDTO {
    private String id;
    private String email;
    private boolean verifiedEmail;
    private String name;
    private String picture;
    private String jwtToken;

    @JsonCreator
    public GoogleUserDTO(
            @JsonProperty("id") String id,
            @JsonProperty("email") String email,
            @JsonProperty("verifiedEmail") boolean verifiedEmail,
            @JsonProperty("name") String name,
            @JsonProperty("picture") String picture) {

        this.id = id;
        this.email = email;
        this.verifiedEmail = verifiedEmail;
        this.name = name;
        this.picture = picture;
    }
}
