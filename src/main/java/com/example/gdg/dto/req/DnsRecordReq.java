package com.example.gdg.dto.req;

import com.example.gdg.type.DnsType;
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
