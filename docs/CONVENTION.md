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

## 이슈 템플릿
- 위치: `.github/ISSUE_TEMPLATE/`
- 형식: `*.md` (레거시 템플릿)
- 제목 접두사: `[FEAT] [BUG] [DOC] [TASK]` 등
- 기본 브랜치에만 적용됨(현재 `develop`).

## PR 템플릿
- 위치: `.github/PULL_REQUEST_TEMPLATE.md`
- 제목 접두사 필수: `[FEAT] [FIX] [DOC] [REFACTOR] [CHORE] [TASK] [BUG]`
- PR 본문은 템플릿에 따라 작성

## 테스트
- 가능한 로컬 테스트 수행 후 체크합니다.
- Swagger 변경이 있으면 확인합니다.
