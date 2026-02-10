package com.example.gdg.domain.auth.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthTokenRes {

    private String tokenType;
    private String accessToken;
    private String refreshToken;
}
