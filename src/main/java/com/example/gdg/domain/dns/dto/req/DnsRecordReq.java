package com.example.gdg.domain.dns.dto.req;

import com.example.gdg.domain.dns.entity.DnsType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DnsRecordReq {

    @NotNull(message = "레코드 타입은 필수입니다.")
    private DnsType type;

    private String host;

    @NotBlank(message = "레코드 값은 필수입니다.")
    private String value;

    @NotNull(message = "TTL은 필수입니다.")
    private Integer ttl;

    private Integer priority;

    @NotNull(message = "프록시 여부는 필수입니다.")
    private Boolean proxied;

    @AssertTrue(message = "TTL은 1(auto) 또는 60~86400 사이여야 합니다.")
    public boolean isTtlValid() {
        return ttl != null && (ttl == 1 || (ttl >= 60 && ttl <= 86400));
    }

    @AssertTrue(message = "MX 레코드는 priority가 필요하고, MX 외 타입은 priority를 비워야 합니다.")
    public boolean isPriorityValid() {
        if (type == null) {
            return true;
        }
        if (type == DnsType.MX) {
            return priority != null;
        }
        return priority == null;
    }
}
