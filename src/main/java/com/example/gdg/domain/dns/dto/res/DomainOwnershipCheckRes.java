package com.example.gdg.domain.dns.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DomainOwnershipCheckRes {

    private String domainName;
    private boolean owned;
}
