package com.example.gdg.domain.auth.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueReq {

    private String refreshToken;
}
