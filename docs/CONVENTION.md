# 프로젝트 컨벤션

## 브랜치
- 기본 브랜치: `develop`
- 브랜치 패턴: `<type>/<name>-#<issue-number>`
- 예시: `feat/login-#123`, `fix/order-#45`
- 타입 예시: `feat`, `fix`, `doc`, `refactor`, `chore`, `task`

## 커밋 메시지
- 형식: `<type>: <summary>`
- type: `feat`, `fix`, `doc`, `refactor`, `chore`, `test`
- 예시: `feat: 사용자 조회 API 추가`

## 이슈
- 템플릿 사용을 원칙으로 합니다.
- 제목 접두사: `[FEAT] [BUG] [DOC] [TASK]` 등

## PR
- 제목 접두사 필수: `[FEAT] [FIX] [DOC] [REFACTOR] [CHORE] [TASK] [BUG]`
- PR 본문은 템플릿에 따라 작성합니다.

## 테스트
- 가능한 로컬 테스트 수행 후 체크합니다.
- Swagger 변경이 있으면 확인합니다.
