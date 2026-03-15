package com.example.gdg.domain.dns.repository;

import com.example.gdg.domain.dns.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DomainRepository extends JpaRepository<Domain, Long> {

    boolean existsByDomainName(String domainName);

    List<Domain> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);
}
