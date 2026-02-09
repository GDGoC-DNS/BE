package com.example.gdg.domain.dns.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class DnsRecord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long domainId;

    @Column(name = "cloudflare_id")
    private String cloudflareId;

    @Enumerated(EnumType.STRING)
    private DnsType type;

    private String host;
    private String value;
    private Integer ttl;
    private Integer priority;
    private Boolean proxied;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public DnsRecord(Long domainId, String cloudflareId, DnsType type, String host, String value, Integer ttl, Integer priority, Boolean proxied) {
        this.domainId = domainId;
        this.cloudflareId = cloudflareId;
        this.type = type;
        this.host = host;
        this.value = value;
        this.ttl = ttl;
        this.priority = priority;
        this.proxied = proxied;
    }

    public void update(String host, String value, Integer ttl, Integer priority, Boolean proxied, DnsType type) {
        this.host = host;
        this.value = value;
        this.ttl = ttl;
        this.priority = priority;
        this.proxied = proxied;
        this.type = type;
    }

    public void updateCloudflareId(String cloudflareId) {
        this.cloudflareId = cloudflareId;
    }
}
