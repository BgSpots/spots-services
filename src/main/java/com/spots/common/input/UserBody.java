package com.spots.common.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserBody {
    private String id;
    private String username;

    @NotBlank(message = "User email can't be blank")
    @Email(message = "Email must be valid!")
    private String email;

    @Size(min = 5, max = 15, message = "Password should be between 5 and 15 characters!")
    private String password;

    private String imageName;

    private long timeUntilNextRoll;

    private long currentSpotId;
}
