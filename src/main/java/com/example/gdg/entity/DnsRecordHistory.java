package com.example.gdg.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DnsRecordHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recordId;
    private Long domainId;

    @Enumerated(EnumType.STRING)
    private ActionType action;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String oldValue;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String newValue;

    private Long changedBy;
    private LocalDateTime changedAt;

    @Builder
    public DnsRecordHistory(Long recordId, Long domainId, ActionType action, String oldValue, String newValue, Long changedBy) {
        this.recordId = recordId;
        this.domainId = domainId;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedBy = changedBy;
        this.changedAt = LocalDateTime.now();
    }
}