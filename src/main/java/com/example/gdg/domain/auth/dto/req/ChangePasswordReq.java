package com.example.gdg.domain.auth.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangePasswordReq {

    @NotBlank(message = "currentPassword is required.")
    private String currentPassword;

    @NotBlank(message = "newPassword is required.")
    private String newPassword;
}
