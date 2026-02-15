package com.example.gdg.domain.auth.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpReq {

    @NotBlank(message = "email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "password is required.")
    private String password;
}
