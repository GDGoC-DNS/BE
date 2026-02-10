package com.example.gdg.domain.auth.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpReq {

    private String email;
    private String password;
}
