package com.example.gdg.domain.audit.repository;

import com.example.gdg.domain.audit.entity.DnsRecordHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DnsRecordHistoryRepository extends JpaRepository<DnsRecordHistory, Long> {
}

