package com.spots.domain;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    @Email private String email;

    @Size(min = 5, max = 15, message = "Password should be between 5 and 15 characters!")
    private String password;

    private String googleToken;
    private boolean emailVerified;
    private boolean isAdmin;
}
