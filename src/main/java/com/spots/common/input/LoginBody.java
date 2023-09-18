package com.spots.common.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class LoginBody {

    @NotBlank(message = "User email can't be blank")
    @Email(message = "Email must be valid!")
    private String email;

    @Size(min = 5, max = 15, message = "Password should be between 5 and 15 characters!")
    String password;

    @JsonIgnore String ip;
}
