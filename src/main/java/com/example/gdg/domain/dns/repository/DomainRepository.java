package com.example.gdg.domain.dns.repository;

import com.example.gdg.domain.dns.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomainRepository extends JpaRepository<Domain, Long> {
}