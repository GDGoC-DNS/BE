package com.example.gdg.domain.dns.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DomainRegisterReq {

    @NotBlank(message = "도메인명은 필수입니다.")
    private String domainName;
}
