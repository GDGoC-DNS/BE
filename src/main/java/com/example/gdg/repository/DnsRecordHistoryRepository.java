package com.example.gdg.repository;

import com.example.gdg.entity.DnsRecordHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DnsRecordHistoryRepository extends JpaRepository<DnsRecordHistory, Long> {
}