package com.example.gdg.domain.dns.service;

import com.example.gdg.domain.dns.dto.req.DomainRegisterReq;
import com.example.gdg.domain.dns.dto.res.DomainOwnershipCheckRes;
import com.example.gdg.domain.dns.dto.res.DomainRegisterRes;
import com.example.gdg.domain.dns.dto.res.DomainSummaryRes;
import com.example.gdg.domain.dns.entity.Domain;
import com.example.gdg.domain.dns.entity.DnsRecord;
import com.example.gdg.domain.dns.repository.DnsRecordRepository;
import com.example.gdg.domain.dns.repository.DomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class DomainService {

    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final Pattern DOMAIN_NAME_PATTERN = Pattern.compile(
            "^(?=.{1,253}$)(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\\.)+[a-z]{2,63}$"
    );

    private final DomainRepository domainRepository;
    private final DnsRecordRepository dnsRecordRepository;
    private final DnsService dnsService;

    public DomainRegisterRes registerDomain(DomainRegisterReq request, Long memberId) {
        String normalizedDomainName = validateAndNormalizeDomainName(request.getDomainName());

        if (domainRepository.existsByDomainName(normalizedDomainName)) {
            throw new IllegalArgumentException("이미 등록된 도메인입니다.");
        }

        Domain domain = domainRepository.save(
                Domain.builder()
                        .memberId(memberId)
                        .domainName(normalizedDomainName)
                        .status(ACTIVE_STATUS)
                        .build()
        );

        return DomainRegisterRes.builder()
                .id(domain.getId())
                .domainName(domain.getDomainName())
                .status(domain.getStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public DomainOwnershipCheckRes checkOwnership(String domainName) {
        String normalizedDomainName = validateAndNormalizeDomainName(domainName);

        return DomainOwnershipCheckRes.builder()
                .domainName(normalizedDomainName)
                .owned(domainRepository.existsByDomainName(normalizedDomainName))
                .build();
    }

    @Transactional(readOnly = true)
    public List<DomainSummaryRes> getMyDomains(Long memberId) {
        return domainRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId).stream()
                .map(domain -> DomainSummaryRes.builder()
                        .id(domain.getId())
                        .domainName(domain.getDomainName())
                        .status(domain.getStatus())
                        .createdAt(domain.getCreatedAt())
                .build())
                .toList();
    }

    public void deleteDomain(Long domainId, Long memberId) {
        Domain domain = getOwnedDomain(domainId, memberId);

        List<Long> recordIds = dnsRecordRepository.findAllByDomainIdOrderByCreatedAtDesc(domainId).stream()
                .map(DnsRecord::getId)
                .toList();

        for (Long recordId : recordIds) {
            dnsService.deleteDnsRecord(domainId, recordId, memberId);
        }

        domainRepository.delete(domain);
    }

    private Domain getOwnedDomain(Long domainId, Long memberId) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new IllegalArgumentException("도메인을 찾을 수 없습니다."));

        if (!domain.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("해당 도메인에 대한 권한이 없습니다.");
        }

        return domain;
    }

    private String validateAndNormalizeDomainName(String domainName) {
        if (domainName == null || domainName.isBlank()) {
            throw new IllegalArgumentException("도메인명은 필수입니다.");
        }

        String normalized = domainName.trim().toLowerCase(Locale.ROOT);
        if (normalized.endsWith(".")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        if (!DOMAIN_NAME_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("유효한 도메인명이 아닙니다.");
        }

        return normalized;
    }
}
