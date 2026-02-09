package com.example.gdg.domain.provider.cloudflare.dto.res;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CloudflareDnsRes {
    private boolean success;
    private CloudflareResult result;

    @Getter
    @NoArgsConstructor
    public static class CloudflareResult {
        private String id;
    }
}
