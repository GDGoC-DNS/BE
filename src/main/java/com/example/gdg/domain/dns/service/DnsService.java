package com.example.gdg.domain.dns.service;

import com.example.gdg.domain.audit.entity.ActionType;
import com.example.gdg.domain.audit.entity.DnsRecordHistory;
import com.example.gdg.domain.audit.repository.DnsRecordHistoryRepository;
import com.example.gdg.domain.dns.dto.req.DnsRecordReq;
import com.example.gdg.domain.dns.entity.DnsRecord;
import com.example.gdg.domain.dns.entity.Domain;
import com.example.gdg.domain.dns.repository.DnsRecordRepository;
import com.example.gdg.domain.dns.repository.DomainRepository;
import com.example.gdg.domain.provider.cloudflare.client.CloudflareApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DnsService {

    private final DnsRecordRepository dnsRecordRepository;
    private final DnsRecordHistoryRepository historyRepository;
    private final DomainRepository domainRepository;
    private final CloudflareApiService cloudflareApiService;
    private final ObjectMapper objectMapper;

    public Long createDnsRecord(DnsRecordReq request, Long memberId) {
        validateDomainOwnership(request.getDomainId(), memberId);

        DnsRecord dnsRecord = DnsRecord.builder()
                .domainId(request.getDomainId())
                .type(request.getType())
                .host(request.getHost())
                .value(request.getValue())
                .ttl(request.getTtl())
                .priority(request.getPriority())
                .proxied(request.getProxied())
                .build();

        String cloudflareId = cloudflareApiService.createRecord(dnsRecord);
        dnsRecord.updateCloudflareId(cloudflareId);
        DnsRecord savedRecord = dnsRecordRepository.save(dnsRecord);
        saveHistory(savedRecord, ActionType.CREATE, null, savedRecord, memberId);

        return savedRecord.getId();
    }

    public void updateDnsRecord(Long recordId, DnsRecordReq request, Long memberId) {
        DnsRecord dnsRecord = dnsRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("레코드를 찾을 수 없습니다."));

        validateDomainOwnership(dnsRecord.getDomainId(), memberId);
        String oldJson = toJson(dnsRecord);

        dnsRecord.update(request.getHost(), request.getValue(), request.getTtl(),
                request.getPriority(), request.getProxied(), request.getType());

        if (dnsRecord.getCloudflareId() != null) {
            cloudflareApiService.updateRecord(dnsRecord.getCloudflareId(), dnsRecord);
        }
        saveHistory(dnsRecord, ActionType.UPDATE, oldJson, dnsRecord, memberId);
    }

    public void deleteDnsRecord(Long recordId, Long memberId) {
        DnsRecord dnsRecord = dnsRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("레코드를 찾을 수 없습니다."));

        validateDomainOwnership(dnsRecord.getDomainId(), memberId);
        String oldJson = toJson(dnsRecord);

        if (dnsRecord.getCloudflareId() != null) {
            cloudflareApiService.deleteRecord(dnsRecord.getCloudflareId());
        }
        dnsRecordRepository.delete(dnsRecord);
        saveHistory(dnsRecord, ActionType.DELETE, oldJson, null, memberId);
    }

    private void validateDomainOwnership(Long domainId, Long memberId) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new IllegalArgumentException("도메인을 찾을 수 없습니다."));

        if (!domain.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("해당 도메인에 대한 권한이 없습니다.");
        }
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