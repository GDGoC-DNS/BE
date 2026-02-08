package com.example.gdg.dto.req;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CloudflareDnsReq {
    private String type;
    private String name;
    private String content;
    private Integer ttl;
    private Boolean proxied;
    private String comment;
}