package com.example.gdg.domain.auth.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueReq {

    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;
}
