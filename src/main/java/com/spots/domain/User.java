package com.spots.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String id;
    private String email;
    private String password;
    private String googleToken;
}
