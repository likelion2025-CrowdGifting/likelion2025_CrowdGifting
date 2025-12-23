# 🎁 CrowdGifting
> 여러 사람이 함께 선물을 준비하는 크라우드 펀딩 기반 선물 서비스
 

## 프로젝트 소개
**CrowdGifting**는 여러 사용자가 하나의 펀딩에 참여하여  
목표 금액을 모아 선물을 구매하는 **크라우드 기반 선물 서비스**

- 초과 결제 방지
- 펀딩 상태 자동 전이
- 결제 검증 및 참여 상태 관리
- AWS S3 이미지 업로드 지원

## 주요 기능

### 사용자
- 회원 가입 / 로그인 (JWT)
- 닉네임 기반 사용자 식별

### 펀딩
- 펀딩 생성 / 조회
- 목표 금액 달성 시 자동 종료
- 사용자가 직접 종료 가능 (STOP)

### 결제
- 결제 대기 → 결제 완료 상태 관리
- 초과 결제 방지 로직
- 결제 검증 후 금액 반영

### 이미지 업로드
- AWS S3 연동
- 펀딩 이미지 업로드


## 펀딩 상태 전이

| 상태 | 설명 |
|---|---|
| `IN_PROGRESS` | 펀딩 진행 중 |
| `ENDED_SUCCESS` | 목표 금액 달성 |
| `ENDED_STOPPED` | 사용자가 직접 종료 |
| `ENDED_EXPIRED` | 마감 기한 초과 |


## SOL Server API
SOL Server는 RESTful API 기반의 서비스 백엔드입니다.  
아래 주소에서 API 서버와 Swagger 문서를 확인할 수 있습니다.

## 🔗 Service URL
- Base URL: http://solserver.store
- Swagger UI: http://solserver.store/swagger-ui/index.html

## 🛠 Tech Stack
- Java 21
- Spring Boot
- Spring Security
- JWT Authentication
- Gradle
- MySQL (or compatible RDBMS)

## API Documentation
Swagger UI를 통해 전체 API 명세, 요청/응답 예시를 확인할 수 있습니다.
 http://solserver.store/swagger-ui/index.html

## Getting Started

### 1. Clone Repository
```bash
git clone <YOUR_REPOSITORY_URL>
cd <PROJECT_DIRECTORY>
```

### 2. Build
```bash
./gradlew build
```

### 3. Run
```bash
./gradlew bootRun
```


## Authentication
- JWT 기반 인증
- 로그인 성공 시 Access Token 발급
- Authorization Header 사용
```
Authorization: Bearer {ACCESS_TOKEN}
```

## 📂 Project Structure (Example)
```
src
 └─ main
    ├─ java
    │  └─ com.example.solserver
    │     ├─ controller
    │     ├─ service
    │     ├─ repository
    │     ├─ domain
    │     └─ global
    └─ resources
       └─ application.yml
```

## Notes
- Swagger 문서는 운영 서버 기준으로 공개되어 있습니다.
- 보안 설정에 따라 일부 API는 인증이 필요합니다.

## Author
- Backend Developer: 김민솔, 오태경
