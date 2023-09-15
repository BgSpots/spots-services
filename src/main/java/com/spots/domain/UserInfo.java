package com.spots.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private long userId;

    private String username;

    private String profilePicture;
}
