package com.example.gdg.controller;

import com.example.gdg.dto.req.DnsRecordReq;
import com.example.gdg.service.DnsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dns")
@RequiredArgsConstructor
@Tag(name = "DNS Management", description = "DNS 레코드 관리 API")
public class DnsController {

    private final DnsService dnsService;

    // 실제로는 로그인한 사용자의 ID를 SecurityContextHolder 등에서 가져와야 함.
    // 여기서는 테스트를 위해 임시로 1L을 사용합니다.
    private final Long MOCK_MEMBER_ID = 1L;

    @PostMapping
    @Operation(summary = "DNS 레코드 생성", description = "DB 저장 & Cloudflare 레코드 생성")
    public ResponseEntity<Long> createDns(@RequestBody DnsRecordReq request) {
        Long id = dnsService.createDnsRecord(request, MOCK_MEMBER_ID);
        return ResponseEntity.ok(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "DNS 레코드 수정", description = "DB 수정 & Cloudflare 레코드 업데이트")
    public ResponseEntity<Void> updateDns(@PathVariable Long id, @RequestBody DnsRecordReq request) {
        dnsService.updateDnsRecord(id, request, MOCK_MEMBER_ID);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "DNS 레코드 삭제", description = "DB 삭제 & Cloudflare 레코드 삭제")
    public ResponseEntity<Void> deleteDns(@PathVariable Long id) {
        dnsService.deleteDnsRecord(id, MOCK_MEMBER_ID);
        return ResponseEntity.ok().build();
    }
}