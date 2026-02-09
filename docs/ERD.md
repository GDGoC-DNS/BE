# DNS Service ERD

> DNS 기반 사이트 제공 서비스 데이터베이스 설계서  
> v0.1.0 | 2026-02-09

---

## 테이블 목록 (4개)

| 도메인 | 테이블 | 설명 |
|---|---|---|
| **member** | `member` | 회원(계정) |
| **managed-domain** | `domain` | 사용자가 등록/관리하는 도메인 |
| **dns** | `dns_record` | DNS 레코드(A/AAAA/CNAME/MX/TXT) |
| **audit** | `dns_record_history` | DNS 레코드 변경 이력(감사 로그) |

---

## ERD 관계도

```text
member
  └─ 1:N ─ domain
            └─ 1:N ─ dns_record
                      └─ 1:N ─ dns_record_history

dns_record_history
  ├─ N:1 ─ dns_record (record_id)
  ├─ N:1 ─ domain     (domain_id)
  └─ N:1 ─ member     (changed_by)

```

## ERD 정의 (DDL)

```sql
-- =========================================================
-- DNS Service ERD - DDL
-- =========================================================

CREATE TABLE member
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    login_id   VARCHAR(100)  NOT NULL,
    email      VARCHAR(255)  NOT NULL,
    password   VARCHAR(255)  NOT NULL,
    created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_member_login_id (login_id),
    UNIQUE KEY uk_member_email (email),
    INDEX      idx_member_created_at (created_at)
) ENGINE=InnoDB COMMENT='회원';

CREATE TABLE domain
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    member_id   BIGINT       NOT NULL,
    domain_name VARCHAR(255) NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_domain_domain_name (domain_name),
    INDEX      idx_domain_member_id (member_id),
    INDEX      idx_domain_status (status),
    CONSTRAINT fk_domain_member
        FOREIGN KEY (member_id) REFERENCES member (id),
    CONSTRAINT chk_domain_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'PENDING', 'SUSPENDED'))
) ENGINE=InnoDB COMMENT='사용자 관리 도메인';

CREATE TABLE dns_record
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    domain_id     BIGINT       NOT NULL,
    cloudflare_id VARCHAR(100) NULL,
    type          ENUM('A','AAAA','CNAME','MX','TXT') NOT NULL,
    host          VARCHAR(255) NOT NULL,
    value         TEXT         NOT NULL,
    ttl           INT          NOT NULL DEFAULT 300,
    priority      INT          NULL,
    proxied       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX         idx_dns_record_domain_id (domain_id),
    INDEX         idx_dns_record_type (type),
    INDEX         idx_dns_record_host (host),
    CONSTRAINT fk_dns_record_domain
        FOREIGN KEY (domain_id) REFERENCES domain (id),
    CONSTRAINT chk_dns_record_ttl
        CHECK (ttl >= 60 AND ttl <= 86400),
    CONSTRAINT chk_dns_record_priority_mx
        CHECK (
            (type = 'MX' AND priority IS NOT NULL)
            OR
            (type <> 'MX' AND priority IS NULL)
        )
) ENGINE=InnoDB COMMENT='DNS 레코드';

CREATE TABLE dns_record_history
(
    id         BIGINT NOT NULL AUTO_INCREMENT,
    record_id  BIGINT NOT NULL,
    domain_id  BIGINT NOT NULL,
    action     ENUM('CREATE','UPDATE','DELETE') NOT NULL,
    old_value  JSON   NULL,
    new_value  JSON   NULL,
    changed_by BIGINT NOT NULL,
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX      idx_dns_record_history_record_id (record_id),
    INDEX      idx_dns_record_history_domain_id (domain_id),
    INDEX      idx_dns_record_history_changed_by (changed_by),
    INDEX      idx_dns_record_history_changed_at (changed_at),
    CONSTRAINT fk_dns_record_history_record
        FOREIGN KEY (record_id) REFERENCES dns_record (id),
    CONSTRAINT fk_dns_record_history_domain
        FOREIGN KEY (domain_id) REFERENCES domain (id),
    CONSTRAINT fk_dns_record_history_member
        FOREIGN KEY (changed_by) REFERENCES member (id)
) ENGINE=InnoDB COMMENT='DNS 레코드 변경 이력(감사 로그)';
```

# Enum 정의
```
dns_record.type: A, AAAA, CNAME, MX, TXT
dns_record_history.action: CREATE, UPDATE, DELETE
```