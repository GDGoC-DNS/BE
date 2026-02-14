package com.example.gdg.domain.auth.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueReq {

    @NotBlank(message = "refreshToken is required.")
    private String refreshToken;
}
