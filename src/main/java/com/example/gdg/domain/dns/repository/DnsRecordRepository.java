package com.example.gdg.domain.dns.repository;

import com.example.gdg.domain.dns.entity.DnsRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DnsRecordRepository extends JpaRepository<DnsRecord, Long> {

    List<DnsRecord> findAllByDomainIdOrderByCreatedAtDesc(Long domainId);
}
