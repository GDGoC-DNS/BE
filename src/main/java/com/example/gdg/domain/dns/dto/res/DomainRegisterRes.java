package com.example.gdg.domain.dns.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DomainRegisterRes {

    private Long id;
    private String domainName;
    private String status;
}
