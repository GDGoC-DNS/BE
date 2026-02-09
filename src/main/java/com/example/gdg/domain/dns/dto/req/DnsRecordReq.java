package com.example.gdg.domain.dns.dto.req;

import com.example.gdg.domain.dns.entity.DnsType;
import lombok.Data;

@Data
public class DnsRecordReq {
    private Long domainId;
    private DnsType type;
    private String host;
    private String value;
    private Integer ttl;
    private Integer priority;
    private Boolean proxied;
}


