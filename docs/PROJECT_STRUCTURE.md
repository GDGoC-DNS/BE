# Project Structure

```text
src/main/java/com/example/gdg
├─ global
│  ├─ config
│  ├─ error
│  ├─ security
│  ├─ common
│  └─ response
│
└─ domain
   ├─ auth                  //인증 인가 관련 도메인
   │  ├─ controller
   │  ├─ service
   │  ├─ dto
   │  └─ token
   │
   ├─ member               //회원 관련 도메인
   │  ├─ entity
   │  ├─ repository
   │  ├─ service
   │  ├─ controller
   │  └─ dto
   │
   ├─ manageddomain         //도메인 관련 도메인
   │  ├─ entity
   │  ├─ repository
   │  ├─ service
   │  ├─ controller
   │  └─ dto
   │
   ├─ dns                   //DNS CORS 관련 도메인
   │  ├─ entity
   │  ├─ repository
   │  ├─ service
   │  ├─ controller
   │  └─ dto
   │
   ├─ provider              //Cloudflare API 호출부
   │  ├─ cloudflare
   │  │  ├─ client
   │  │  ├─ dto
   │  │  └─ mapper
   │  └─ service
   │
   └─ audit                 //기록 관련 도메인
      ├─ entity
      ├─ repository
      ├─ service
      └─ listener
```
//선민님 작업 내용 이동 참고해주세요!!
## Current DNS-related code mapping
- `DnsController` -> `domain/dns/controller`
- `DnsService` -> `domain/dns/service`
- `DnsRecord`, `DnsType`, `DnsRecordReq`, `DnsRecordRepository` -> `domain/dns`
- `CloudflareApiService`, `CloudflareDnsReq`, `CloudflareDnsRes` -> `domain/provider/cloudflare`
- `DnsRecordHistory`, `ActionType`, `DnsRecordHistoryRepository` -> `domain/audit`
