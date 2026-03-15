package com.example.gdg.domain.dns.controller;

import com.example.gdg.domain.dns.dto.req.DnsRecordReq;
import com.example.gdg.domain.dns.dto.res.DnsRecordSummaryRes;
import com.example.gdg.domain.dns.service.DnsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domains/{domainId}/dns")
@RequiredArgsConstructor
@Tag(name = "DNS Management", description = "DNS 레코드 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class DnsController {

    private final DnsService dnsService;

    @GetMapping
    @Operation(summary = "DNS 레코드 목록 조회", description = "특정 도메인에 속한 DNS 레코드 목록과 각 레코드 id를 반환합니다.")
    public ResponseEntity<List<DnsRecordSummaryRes>> getDnsRecords(
            @PathVariable Long domainId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(dnsService.getDnsRecords(domainId, resolveMemberId(authentication)));
    }

    @PostMapping
    @Operation(summary = "DNS 레코드 생성", description = "DB 저장 및 Cloudflare 레코드 생성")
    public ResponseEntity<Long> createDns(
            @PathVariable Long domainId,
            @Valid @RequestBody DnsRecordReq request,
            Authentication authentication
    ) {
        Long id = dnsService.createDnsRecord(domainId, request, resolveMemberId(authentication));
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "DNS 레코드 수정", description = "DB 수정 및 Cloudflare 레코드 업데이트")
    public ResponseEntity<Void> updateDns(
            @PathVariable Long domainId,
            @PathVariable Long id,
            @Valid @RequestBody DnsRecordReq request,
            Authentication authentication
    ) {
        dnsService.updateDnsRecord(domainId, id, request, resolveMemberId(authentication));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "DNS 레코드 삭제", description = "DB 삭제 및 Cloudflare 레코드 삭제")
    public ResponseEntity<Void> deleteDns(
            @PathVariable Long domainId,
            @PathVariable Long id,
            Authentication authentication
    ) {
        dnsService.deleteDnsRecord(domainId, id, resolveMemberId(authentication));
        return ResponseEntity.ok().build();
    }

    private Long resolveMemberId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}
