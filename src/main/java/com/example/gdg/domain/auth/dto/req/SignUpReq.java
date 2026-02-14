package com.example.gdg.domain.auth.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpReq {

    @NotBlank(message = "email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "password is required.")
    @Size(min = 8, message = "password must be at least 8 characters.")
    private String password;
}
