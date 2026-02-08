package com.example.gdg.repository;

import com.example.gdg.entity.DnsRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DnsRecordRepository extends JpaRepository<DnsRecord, Long> {
}