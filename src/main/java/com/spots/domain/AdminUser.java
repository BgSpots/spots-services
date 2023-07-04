package com.spots.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUser {
    private String id;
    private String email;
    private String password;
    private boolean emailVerified;
}
