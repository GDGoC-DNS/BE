package com.example.gdg.domain.dns.controller;

import com.example.gdg.domain.dns.dto.req.DnsRecordReq;
import com.example.gdg.domain.dns.service.DnsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dns")
@RequiredArgsConstructor
@Tag(name = "DNS Management", description = "DNS 레코드 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class DnsController {

    private final DnsService dnsService;

    @PostMapping
    @Operation(summary = "DNS 레코드 생성", description = "DB 저장 및 Cloudflare 레코드 생성")
    public ResponseEntity<Long> createDns(@RequestBody DnsRecordReq request, Authentication authentication) {
        Long id = dnsService.createDnsRecord(request, resolveMemberId(authentication));
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "DNS 레코드 수정", description = "DB 수정 및 Cloudflare 레코드 업데이트")
    public ResponseEntity<Void> updateDns(@PathVariable Long id, @RequestBody DnsRecordReq request, Authentication authentication) {
        dnsService.updateDnsRecord(id, request, resolveMemberId(authentication));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "DNS 레코드 삭제", description = "DB 삭제 및 Cloudflare 레코드 삭제")
    public ResponseEntity<Void> deleteDns(@PathVariable Long id, Authentication authentication) {
        dnsService.deleteDnsRecord(id, resolveMemberId(authentication));
        return ResponseEntity.ok().build();
    }

    private Long resolveMemberId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}
