package com.example.gdg.domain.dns.dto.res;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DomainSummaryRes {

    private Long id;
    private String domainName;
    private String status;
    private LocalDateTime createdAt;
}
