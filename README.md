# Comment Service

댓글 관리 기능을 제공하는 마이크로서비스입니다.

## 🚀 기술 스택

- **Java 17**
- **Spring Boot 3.4.5**
- **Spring Cloud 2024.0.1**
- **MongoDB** (댓글 데이터 저장)
- **Apache Kafka** (이벤트 메시징)
- **Netflix Eureka** (서비스 디스커버리)
- **OpenFeign** (서비스 간 통신)
- **Gradle** (빌드 도구)
- **Docker** (컨테이너화)

## 📋 주요 기능

### 댓글 관리
- ✅ 댓글 생성
- ✅ 댓글 수정
- ✅ 댓글 삭제 (Soft Delete)
- ✅ 댓글 조회 (단일/목록)
- ✅ 댓글 좋아요/좋아요 취소
- ✅ 댓글 좋아요 개수 조회
- ✅ 댓글 좋아요 여부 확인

### 정렬 및 페이징
- ✅ 최신순 정렬 (RECENT)
- ✅ 인기순 정렬 (POPULAR)
- ✅ 페이징 처리 (기본 10개씩)
- ✅ 삭제된 댓글 필터링

### 이벤트 처리
- ✅ 댓글 생성 이벤트 발행
- ✅ 댓글 삭제 이벤트 발행
- ✅ 비동기 이벤트 처리
- ✅ 트랜잭션 기반 이벤트 처리

## 🏗️ 아키텍처

### Clean Architecture
```
src/main/java/com/example/commentservice/
├── domain/                    # 도메인 계층
│   └── comment/
│       ├── entity/           # 엔티티
│       ├── dto/              # 데이터 전송 객체
│       ├── application/      # 애플리케이션 서비스
│       ├── infrastructure/   # 인프라스트럭처
│       └── presentation/     # 프레젠테이션 계층
├── common/                   # 공통 모듈
│   ├── config/              # 설정
│   ├── entity/              # 공통 엔티티
│   ├── exception/           # 예외 처리
│   ├── kafka/               # Kafka 관련
│   └── response/            # 응답 모델
└── client/                  # 외부 서비스 클라이언트
```

### MSA 원칙 적용
- **서비스 독립성**: Post 서비스와의 결합도 최소화
- **이벤트 기반 통신**: Kafka를 통한 비동기 이벤트 처리
- **장애 격리**: 서비스 간 장애 전파 방지

## 🚀 실행 방법

### 1. 환경 요구사항
- Java 17 이상
- MongoDB
- Apache Kafka
- Netflix Eureka Server

### 2. 로컬 실행
```bash
# 프로젝트 클론
git clone https://github.com/Learn-Run/comment-service.git
cd comment-service

# 빌드
./gradlew build

# 실행
./gradlew bootRun
```

### 3. Docker 실행
```bash
# Docker 이미지 빌드
docker build -t comment-service .

# Docker 실행
docker run -p 8080:8080 comment-service
```

### 4. Docker Compose 실행
```bash
# 전체 서비스 실행
docker-compose -f docker-compose-comment.yml up -d
```

## 📡 API 문서

### Swagger UI
- URL: `http://localhost:8080/swagger-ui.html`
- API 문서 자동 생성

### 주요 API 엔드포인트

#### 댓글 관리
```
POST   /api/v1/comment/post/{postUuid}     # 댓글 생성
PATCH  /api/v1/comment/{commentUuid}       # 댓글 수정
DELETE /api/v1/comment/{commentUuid}       # 댓글 삭제
GET    /api/v1/comment/{commentUuid}       # 댓글 조회
GET    /api/v1/comment/post/{postUuid}/list # 댓글 목록 조회
```

#### 댓글 좋아요
```
POST   /api/v1/comment-like/{commentUuid}  # 댓글 좋아요
DELETE /api/v1/comment-like/{commentUuid}  # 댓글 좋아요 취소
GET    /api/v1/comment-like/{commentUuid}/count # 좋아요 개수
GET    /api/v1/comment-like/{commentUuid}/check # 좋아요 여부
```

## ⚙️ 설정

### 환경별 설정 파일
- `application.yml` - 기본 설정
- `application-dev.yml` - 개발 환경
- `application-prod.yml` - 운영 환경

### 주요 설정 항목
```yaml
spring:
  application:
    name: comment-service
  data:
    mongodb:
      uri: mongodb://localhost:27017/learn_run_comment
  kafka:
    bootstrap-servers: localhost:9092
    topics:
      comment-created: comment-created
      comment-deleted: comment-deleted

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

## 🧪 테스트

### 단위 테스트
```bash
./gradlew test
```

### 통합 테스트
```bash
./gradlew integrationTest
```

## 📊 모니터링

### 헬스체크
- URL: `http://localhost:8080/actuator/health`

### 메트릭
- URL: `http://localhost:8080/actuator/metrics`

## 🔧 개발 가이드

### 코드 컨벤션
- **네이밍**: camelCase 사용
- **패키지**: 도메인 중심 패키지 구조
- **예외 처리**: BaseException 상속
- **로깅**: SLF4J + Logback 사용

### 트랜잭션 처리
- **읽기 전용**: `@Transactional(readOnly = true)`
- **쓰기 작업**: `@Transactional(isolation = Isolation.READ_COMMITTED)`
- **이벤트 처리**: `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`

### 이벤트 처리
- **비동기 처리**: `@Async` 사용
- **전용 스레드 풀**: `commentEventExecutor`
- **예외 처리**: 이벤트 처리 실패 시 로깅

## 🚀 배포

### CI/CD 파이프라인
- **GitHub Actions**를 통한 자동 배포
- **AWS ECR**에 Docker 이미지 푸시
- **AWS EC2**에 자동 배포

### 배포 환경
- **개발**: 개발 서버
- **스테이징**: 스테이징 서버
- **운영**: AWS EC2

## 📝 변경 이력

### v1.0.0 (2024-01-XX)
- ✅ 댓글 CRUD 기능 구현
- ✅ 댓글 좋아요 기능 구현
- ✅ 페이징 및 정렬 기능 구현
- ✅ 이벤트 기반 아키텍처 적용
- ✅ MSA 원칙 적용 (Post 서비스 의존성 제거)
- ✅ 트랜잭션 처리 개선
- ✅ 비동기 이벤트 처리 구현

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 👥 팀

- **개발팀**: Learn-Run Team
- **문의**: [이메일 주소]

## 🔗 관련 링크

- [API 문서](http://localhost:8080/swagger-ui.html)
- [프로젝트 이슈](https://github.com/Learn-Run/comment-service/issues)
- [위키](https://github.com/Learn-Run/comment-service/wiki) 