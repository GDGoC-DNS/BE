package com.example.gdg.domain.dns.service;

import com.example.gdg.domain.audit.entity.ActionType;
import com.example.gdg.domain.audit.entity.DnsRecordHistory;
import com.example.gdg.domain.audit.repository.DnsRecordHistoryRepository;
import com.example.gdg.domain.dns.dto.req.DnsRecordReq;
import com.example.gdg.domain.dns.dto.res.DnsRecordSummaryRes;
import com.example.gdg.domain.dns.entity.DnsRecord;
import com.example.gdg.domain.dns.entity.Domain;
import com.example.gdg.domain.dns.repository.DnsRecordRepository;
import com.example.gdg.domain.dns.repository.DomainRepository;
import com.example.gdg.domain.provider.cloudflare.client.CloudflareApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class DnsService {

    private final DnsRecordRepository dnsRecordRepository;
    private final DnsRecordHistoryRepository historyRepository;
    private final DomainRepository domainRepository;
    private final CloudflareApiService cloudflareApiService;
    private final ObjectMapper objectMapper;

    public Long createDnsRecord(Long domainId, DnsRecordReq request, Long memberId) {
        Domain domain = validateDomainOwnership(domainId, memberId);

        DnsRecord dnsRecord = DnsRecord.builder()
                .domainId(domainId)
                .type(request.getType())
                .host(normalizeHost(request.getHost()))
                .value(request.getValue())
                .ttl(request.getTtl())
                .priority(request.getPriority())
                .proxied(request.getProxied())
                .build();

        String cloudflareId = cloudflareApiService.createRecord(resolveRecordName(domain, dnsRecord.getHost()), dnsRecord);
        dnsRecord.updateCloudflareId(cloudflareId);
        DnsRecord savedRecord = dnsRecordRepository.save(dnsRecord);
        saveHistory(savedRecord, ActionType.CREATE, null, savedRecord, memberId);

        return savedRecord.getId();
    }

    public void updateDnsRecord(Long domainId, Long recordId, DnsRecordReq request, Long memberId) {
        Domain domain = validateDomainOwnership(domainId, memberId);
        DnsRecord dnsRecord = findDnsRecord(recordId);
        validateRecordBelongsToDomain(dnsRecord, domainId);

        String oldJson = toJson(dnsRecord);

        dnsRecord.update(normalizeHost(request.getHost()), request.getValue(), request.getTtl(),
                request.getPriority(), request.getProxied(), request.getType());

        if (dnsRecord.getCloudflareId() != null) {
            cloudflareApiService.updateRecord(resolveRecordName(domain, dnsRecord.getHost()), dnsRecord.getCloudflareId(), dnsRecord);
        }
        saveHistory(dnsRecord, ActionType.UPDATE, oldJson, dnsRecord, memberId);
    }

    public void deleteDnsRecord(Long domainId, Long recordId, Long memberId) {
        validateDomainOwnership(domainId, memberId);
        DnsRecord dnsRecord = findDnsRecord(recordId);
        validateRecordBelongsToDomain(dnsRecord, domainId);

        String oldJson = toJson(dnsRecord);

        if (dnsRecord.getCloudflareId() != null) {
            cloudflareApiService.deleteRecord(dnsRecord.getCloudflareId());
        }
        dnsRecordRepository.delete(dnsRecord);
        saveHistory(dnsRecord, ActionType.DELETE, oldJson, null, memberId);
    }

    @Transactional(readOnly = true)
    public List<DnsRecordSummaryRes> getDnsRecords(Long domainId, Long memberId) {
        Domain domain = validateDomainOwnership(domainId, memberId);

        return dnsRecordRepository.findAllByDomainIdOrderByCreatedAtDesc(domainId).stream()
                .map(record -> DnsRecordSummaryRes.builder()
                        .id(record.getId())
                        .host(record.getHost())
                        .recordName(resolveRecordName(domain, record.getHost()))
                        .type(record.getType())
                        .value(record.getValue())
                        .ttl(record.getTtl())
                        .priority(record.getPriority())
                        .proxied(record.getProxied())
                        .createdAt(record.getCreatedAt())
                        .updatedAt(record.getUpdatedAt())
                        .build())
                .toList();
    }

    private Domain validateDomainOwnership(Long domainId, Long memberId) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new IllegalArgumentException("도메인을 찾을 수 없습니다."));

        if (!domain.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("해당 도메인에 대한 권한이 없습니다.");
        }

        return domain;
    }

    private DnsRecord findDnsRecord(Long recordId) {
        return dnsRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("레코드를 찾을 수 없습니다."));
    }

    private void validateRecordBelongsToDomain(DnsRecord dnsRecord, Long domainId) {
        if (!dnsRecord.getDomainId().equals(domainId)) {
            throw new IllegalArgumentException("해당 도메인에 속한 레코드가 아닙니다.");
        }
    }

    private String normalizeHost(String host) {
        if (host == null) {
            return "";
        }

        String normalized = host.trim().toLowerCase(Locale.ROOT);
        return "@".equals(normalized) ? "" : normalized;
    }

    private String resolveRecordName(Domain domain, String host) {
        if (host == null || host.isBlank()) {
            return domain.getDomainName();
        }

        String normalizedDomainName = domain.getDomainName().toLowerCase(Locale.ROOT);
        if (host.equals(normalizedDomainName) || host.endsWith("." + normalizedDomainName)) {
            return host;
        }

        return host + "." + normalizedDomainName;
    }

    private void saveHistory(DnsRecord record, ActionType action, String oldJson, DnsRecord newRecordState, Long memberId) {
        String newJson = (action == ActionType.DELETE) ? null : toJson(newRecordState);

        DnsRecordHistory history = DnsRecordHistory.builder()
                .recordId(record.getId())
                .domainId(record.getDomainId())
                .action(action)
                .oldValue(oldJson)
                .newValue(newJson)
                .changedBy(memberId)
                .build();

        historyRepository.save(history);
    }

    private String toJson(Object object) {
        try {
            if (object == null) return null;
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return "{}";
        }
    }
}
