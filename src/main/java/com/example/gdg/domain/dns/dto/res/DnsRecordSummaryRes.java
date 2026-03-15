package com.example.gdg.domain.dns.dto.res;

import com.example.gdg.domain.dns.entity.DnsType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DnsRecordSummaryRes {

    private Long id;
    private String host;
    private String recordName;
    private DnsType type;
    private String value;
    private Integer ttl;
    private Integer priority;
    private Boolean proxied;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
