package com.example.gdg.domain.dns.repository;

import com.example.gdg.domain.dns.entity.DnsRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DnsRecordRepository extends JpaRepository<DnsRecord, Long> {
}

