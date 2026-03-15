package com.example.gdg.domain.dns.controller;

import com.example.gdg.domain.dns.dto.req.DomainRegisterReq;
import com.example.gdg.domain.dns.dto.res.DomainOwnershipCheckRes;
import com.example.gdg.domain.dns.dto.res.DomainRegisterRes;
import com.example.gdg.domain.dns.dto.res.DomainSummaryRes;
import com.example.gdg.domain.dns.service.DomainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domains")
@RequiredArgsConstructor
@Tag(name = "Domain Management", description = "도메인 등록/관리 API")
@SecurityRequirement(name = "bearerAuth")
public class DomainController {

    private final DomainService domainService;

    @GetMapping("/ownership")
    @SecurityRequirements
    @Operation(summary = "도메인 소유 여부 조회 API", description = "특정 도메인이 이미 등록되어 있는지 반환합니다.")
    public ResponseEntity<DomainOwnershipCheckRes> checkOwnership(@RequestParam String domainName) {
        return ResponseEntity.ok(domainService.checkOwnership(domainName));
    }

    @GetMapping
    @Operation(summary = "내 도메인 목록 조회 API", description = "현재 로그인한 사용자가 등록한 도메인 목록을 반환합니다.")
    public ResponseEntity<List<DomainSummaryRes>> getMyDomains(Authentication authentication) {
        return ResponseEntity.ok(domainService.getMyDomains(Long.parseLong(authentication.getName())));
    }

    @PostMapping
    @Operation(summary = "도메인 등록 API", description = "현재 로그인한 사용자에게 도메인을 등록합니다.")
    public ResponseEntity<DomainRegisterRes> registerDomain(
            @Valid @RequestBody DomainRegisterReq request,
            Authentication authentication
    ) {
        DomainRegisterRes response = domainService.registerDomain(request, Long.parseLong(authentication.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{domainId}")
    @Operation(summary = "도메인 삭제 API", description = "현재 로그인한 사용자의 도메인과 연결된 DNS 레코드를 함께 삭제합니다.")
    public ResponseEntity<Void> deleteDomain(@PathVariable Long domainId, Authentication authentication) {
        domainService.deleteDomain(domainId, Long.parseLong(authentication.getName()));
        return ResponseEntity.noContent().build();
    }
}
