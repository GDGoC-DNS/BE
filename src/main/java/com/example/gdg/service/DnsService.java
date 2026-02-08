package com.example.gdg.service;

import com.example.gdg.dto.req.DnsRecordReq;
import com.example.gdg.entity.*;
import com.example.gdg.repository.*;
import com.example.gdg.entity.ActionType;
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
    private final CloudflareApiService cloudflareApiService;
    private final ObjectMapper objectMapper;

    // 생성
    public Long createDnsRecord(DnsRecordReq request, Long memberId) {
        DnsRecord dnsRecord = DnsRecord.builder()
                .domainId(request.getDomainId())
                .type(request.getType())
                .host(request.getHost())
                .value(request.getValue())
                .ttl(request.getTtl())
                .priority(request.getPriority())
                .proxied(request.getProxied())
                .build();

        // 1. Cloudflare 호출
        String cloudflareId = cloudflareApiService.createRecord(dnsRecord);
        dnsRecord.updateCloudflareId(cloudflareId);

        // 2. DB 저장
        DnsRecord savedRecord = dnsRecordRepository.save(dnsRecord);

        // 3. 히스토리
        saveHistory(savedRecord, ActionType.CREATE, null, savedRecord, memberId);

        return savedRecord.getId();
    }

    // 수정
    public void updateDnsRecord(Long recordId, DnsRecordReq request, Long memberId) {
        DnsRecord dnsRecord = dnsRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found"));

        String oldJson = toJson(dnsRecord);

        dnsRecord.update(request.getHost(), request.getValue(), request.getTtl(),
                request.getPriority(), request.getProxied(), request.getType());

        // 1. Cloudflare 호출
        if (dnsRecord.getCloudflareId() != null) {
            cloudflareApiService.updateRecord(dnsRecord.getCloudflareId(), dnsRecord);
        }

        // 2. 히스토리 (트랜잭션 커밋 시 DB 업데이트 됨)
        saveHistory(dnsRecord, ActionType.UPDATE, oldJson, dnsRecord, memberId);
    }

    // 삭제
    public void deleteDnsRecord(Long recordId, Long memberId) {
        DnsRecord dnsRecord = dnsRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found"));

        String oldJson = toJson(dnsRecord);

        // 1. Cloudflare 호출
        if (dnsRecord.getCloudflareId() != null) {
            cloudflareApiService.deleteRecord(dnsRecord.getCloudflareId());
        }

        // 2. DB 삭제
        dnsRecordRepository.delete(dnsRecord);

        // 3. 히스토리
        DnsRecordHistory history = DnsRecordHistory.builder()
                .recordId(dnsRecord.getId())
                .domainId(dnsRecord.getDomainId())
                .action(ActionType.DELETE)
                .oldValue(oldJson)
                .newValue(null)
                .changedBy(memberId)
                .build();
        historyRepository.save(history);
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