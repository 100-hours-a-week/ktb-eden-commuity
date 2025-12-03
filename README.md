## 📌 Eden – 커뮤니티 서비스 (Backend)

>**Spring Boot + Spring Security + JPA + JWT 기반의 회원제 커뮤니티 서비스**<br />
Eden은 회원가입 후 게시글 생성·수정·삭제, 좋아요, 댓글 기능을 사용할 수 있는 **회원 기반 커뮤니티 플랫폼**입니다.<br />
비회원은 게시글 조회까지만 가능합니다.<br />
------------
## 🚀 프로젝트 소개

Eden은 회원 기반의 커뮤니티 서비스로,<br />
게시글 조회는 비회원도 가능하지만 글 작성·댓글·좋아요 등 모든 활동은 회원 인증이 필수입니다.<br />

백엔드는 **DDD-Lite 아키텍처, JWT 기반 인증 구조,<br />
무한스크롤 기반 게시글 조회, 성능 최적화**를 목표로 개발했습니다.<br />

또한 JPA 기반의 Repository 구조와 InMemory Repository를 분리하여<br />
운영·테스트 환경을 명확히 나누는 아키텍처를 설계했습니다.<br />

------------
## 🔨 프로젝트 구조
<details>
  <summary>패키지 구조</summary>
    
```
└── restapi_subject
    ├── domain
    │   ├── auth
    │   │   ├── controller
    │   │   │   └── AuthController.java
    │   │   ├── domain
    │   │   │   └── RefreshToken.java
    │   │   ├── dto
    │   │   │   ├── AuthReq.java
    │   │   │   └── AuthRes.java
    │   │   ├── infra
    │   │   │   └── RefreshTokenEntity.java
    │   │   ├── jwt
    │   │   │   └── JwtAuthFilter.java
    │   │   ├── repository
    │   │   │   ├── impl
    │   │   │   │   ├── InMemoryRefreshTokenStoreImpl.java
    │   │   │   │   └── JpaRefreshTokenStoreImpl.java
    │   │   │   ├── RefreshTokenJpaRepository.java
    │   │   │   └── RefreshTokenRepository.java
    │   │   └── service
    │   │       ├── AuthService.java
    │   │       └── RefreshTokenService.java
    │   ├── board
    │   │   ├── controller
    │   │   │   └── BoardController.java
    │   │   ├── domain
    │   │   │   └── Board.java
    │   │   ├── dto
    │   │   │   ├── BoardReq.java
    │   │   │   └── BoardRes.java
    │   │   ├── event
    │   │   │   └── BoardEventListener.java
    │   │   ├── infra
    │   │   │   └── BoardEntity.java
    │   │   ├── repository
    │   │   │   ├── BoardJpaRepository.java
    │   │   │   ├── BoardRepository.java
    │   │   │   └── impl
    │   │   │       ├── InMemoryBoardRepositoryImpl.java
    │   │   │       └── JpaBoardRepositoryImpl.java
    │   │   └── service
    │   │       ├── BoardManagementFacade.java
    │   │       ├── BoardService.java
    │   │       └── BoardValidator.java
    │   ├── boardlike
    │   │   ├── controller
    │   │   │   └── BoardLikeController.java
    │   │   ├── domain
    │   │   │   └── BoardLike.java
    │   │   ├── dto
    │   │   │   └── BoardLikeResDto.java
    │   │   ├── event
    │   │   │   ├── BoardLikeEvent.java
    │   │   │   └── BoardLikeEventListener.java
    │   │   ├── infra
    │   │   │   └── BoardLikeEntity.java
    │   │   ├── repository
    │   │   │   ├── BoardLikeJpaRepository.java
    │   │   │   ├── BoardLikeRepository.java
    │   │   │   └── impl
    │   │   │       ├── InMemoryBoardLikeRepositoryImpl.java
    │   │   │       └── JpaBoardLikeRepositoryImpl.java
    │   │   └── service
    │   │       └── BoardLikeService.java
    │   ├── comment
    │   │   ├── controller
    │   │   │   └── CommentController.java
    │   │   ├── domain
    │   │   │   └── Comment.java
    │   │   ├── dto
    │   │   │   ├── CommentReq.java
    │   │   │   └── CommentRes.java
    │   │   ├── event
    │   │   │   ├── CommentEvent.java
    │   │   │   └── CommentEventListener.java
    │   │   ├── infra
    │   │   │   └── CommentEntity.java
    │   │   ├── repository
    │   │   │   ├── CommentJpaRepository.java
    │   │   │   ├── CommentRepository.java
    │   │   │   └── impl
    │   │   │       ├── InMemoryCommentRepositoryImpl.java
    │   │   │       └── JpaCommentRepositoryImpl.java
    │   │   └── service
    │   │       ├── CommentManagementFacade.java
    │   │       └── CommentService.java
    │   ├── file
    │   │   ├── contorller
    │   │   │   └── FileController.java
    │   │   └── service
    │   │       └── FileStorageService.java
    │   └── user
    │       ├── controller
    │       │   └── UserController.java
    │       ├── domain
    │       │   └── User.java
    │       ├── dto
    │       │   ├── UserReq.java
    │       │   └── UserRes.java
    │       ├── event
    │       │   └── UserEvent.java
    │       ├── infra
    │       │   └── UserEntity.java
    │       ├── repository
    │       │   ├── impl
    │       │   │   ├── InMemoryUserRepositoryImpl.java
    │       │   │   └── JpaUserRepositoryImpl.java
    │       │   ├── UserJpaRepository.java
    │       │   └── UserRepository.java
    │       └── service
    │           └── UserService.java
    ├── global
    │   ├── common
    │   │   ├── dto
    │   │   │   └── PageCursor.java
    │   │   ├── entity
    │   │   │   ├── BaseEntity.java
    │   │   │   └── JpaBaseEntity.java
    │   │   ├── repository
    │   │   │   ├── BaseInMemoryRepository.java
    │   │   │   ├── CrudCustomRepository.java
    │   │   │   └── InMemoryStorage.java
    │   │   └── response
    │   │       └── ApiResponse.java
    │   ├── config
    │   │   ├── CookieProperties.java
    │   │   ├── JpaAuditingConfig.java
    │   │   ├── SecurityConfig.java
    │   │   ├── SwaggerConfig.java
    │   │   └── WebMvcConfig.java
    │   ├── error
    │   │   ├── exception
    │   │   │   ├── CustomException.java
    │   │   │   └── ExceptionType.java
    │   │   └── handler
    │   │       └── GlobalExceptionHandler.java
    │   └── util
    │       ├── CookieUtil.java
    │       ├── JwtUtil.java
    │       ├── PasswordUtil.java
    │       ├── ResponseUtil.java
    │       └── TokenResponseWriter.java
    └── RestapiSubjectApplication.java
```
- YML
```
└── resources
    ├── application-local.yml
    ├── application-prod.yml
    ├── application-secret.yml
    └── application.yml
```
- test
```
└── restapi_subject
    ├── domain
    │   ├── auth
    │   │   ├── controller
    │   │   │   ├── AuthControllerIntegrationTest.java
    │   │   │   └── AuthControllerTest.java
    │   │   ├── jwt
    │   │   │   └── JwtAuthFilterTest.java
    │   │   └── service
    │   │       ├── AuthServiceTestMocking.java
    │   │       └── AuthServiceTestSpringBootTest.java
    │   └── user
    │       ├── controller
    │       │   └── UserControllerTest.java
    │       └── service
    │           └── UserServiceTest.java
    └── RestapiSubjectApplicationTests.java

```

</details>
<details>
  <summary>데이터베이스 모델링 (ERD)</summary>

  <img width="818" height="736" alt="image" src="https://github.com/user-attachments/assets/33604ed8-b279-4079-ba13-7f4119bfbefa" />
  
</details>


## 시작 가이드
 ### 설치
1. 저장소 클론
2. 백엔드 설정<br />
`application-local.yml` - 개발환경<br />
`application-prod.yml` - 운영환경<br />
`application-secret.yml` - jwt, datasource<br />
    ```
    spring:
      datasource:
        username: {{your_database_username}}
        password: {{your_database_password}}
      jwt:
        secret: {{your_jwt_secret_key}}
    ```

3. Gradle 실행
    ```
    ./gradlew clean build
    ./gradlew bootRun
    ```

## 🏗 기술 스택
### Backend

+ Java 17
+ Spring Boot 3
+ Spring Web
+ Spring Security
+ Spring Data JPA (Hibernate)
+ MySQL 8
+ JWT (Access / Refresh Token)
+ Gradle

### Frontend

+ HTML / CSS / Vanilla JS
+ Fetch API
+ Figma 기반 디자인

------------

## 🧩 시스템 아키텍처
```
Client (Vanilla JS)
       ↓
   REST API
       ↓
Spring Boot Backend
       ↓
Spring Data JPA
       ↓
    MySQL DB

```

## ⭐ 주요 기능
### 🔐 1. 회원 인증 / 보안 (Authentication & Authorization)
### ✔ JWT 기반 인증 (Access · Refresh Token)
+ Spring Security + JWT 기반 로그인/로그아웃 구조
+ AccessToken + RefreshToken 조합
+ HTTP-Only Cookie 기반 리프레시 토큰 관리
+ RTR(Rotate Refresh Token) 전략 적용 → 탈취 위험 최소화
+ 만료된 AccessToken + RefreshToken 조합으로 갱신하는 구조 구현

### ✔ 회원 가입 / 로그인 / 로그아웃
+ BCrypt 암호화 저장
+ 이메일 중복 체크, 비밀번호 정책 검증
+ 로그아웃 시 Refresh Token 즉시 무효화

### ✔ Soft Delete 기반 회원탈퇴
+ 삭제 회원의 데이터는 즉시 식별 불가능하게 변환<br />
(email → deleted_{{uuid}}, nickname → 탈퇴한 사용자)
+ 연관된 게시글/댓글 Soft Delete 처리


## 📰 2. 게시글(Board) 기능
### ✔ CRUD 기능
+ 게시글 생성 / 수정 / 삭제
+ 게시글 전체 조회 / 단일 상세 조회
+ 이미지 업로드 (Multipart + 로컬 스토리지)

### ✔ 무한스크롤 기반 Cursor Pagination
+ Offset 기반이 아닌 커서 기반 페이지네이션 적용
+ 게시글 ID 기준으로 더 효율적인 조회 성능 달성

### ✔ 게시글 상세 정보
+ 게시글 내용 + 작성자 프로필
+ 좋아요 여부(likedByMe), 좋아요 개수, 조회수, 댓글 개수 포함
+ 조회수 증가는 DB Atomic Update 방식 적용
(동시성 안정성 확보)

### ✔ N+1 문제 해결
+ 게시글 목록 조회: JOIN FETCH author
+ 게시글 단건 조회: 게시글 + 작성자 + 댓글 + 댓글 작성자 <br />
모두 fetch join으로 한 번에 조회
+ 댓글 로딩 방식은 사용 시점에 따라 전략적으로 분리 가능

## 💬 3. 댓글(Comment) 기능
### ✔ 댓글 CRUD
+ 댓글 생성, 수정, 삭제
+ Soft Delete 적용 → “삭제된 댓글입니다.”로 표시

### ✔ 페이지네이션 적용
+ 댓글은 별도 API로 페이지 단위 조회
+ CommentManagementFacade에서 작성자 프로필 batch 조회하여 N+1 방지

### ✔ 작성자 프로필 매핑
+ 댓글 작성자의 닉네임, 프로필 이미지 포함하여 반환
+ 탈퇴 사용자 처리

## ❤️ 4. 좋아요(Like) 기능
### ✔ 게시글 좋아요 / 좋아요 취소
+ 좋아요 추가/삭제 시 DL(도메인 이벤트) 구조로 count 업데이트
+ 좋아요한 게시글 ID 목록을 한 번에 조회하여
+ 게시글 리스트 likedByMe 처리 → N+1 방지


## 👤 5. 사용자(User) 기능
### ✔ 프로필 조회
+ 닉네임 / 이메일 / 프로필 이미지 조회

### ✔ 프로필 수정
+ 닉네임 변경
+ 프로필 이미지 업데이트

## 🗂 6. 파일 업로드 (File Storage)
### ✔ 프로필 이미지 & 게시글 이미지 업로드
+ 이미지 서버 로컬 저장 방식
+ UUID 파일명 + 확장자 유지
+ 파일 접근 URL 자동 생성


## ⚙ 7. 인프라 & 아키텍처 특징
### ✔ DDD-Lite 구조
+ 도메인, 인프라(JPA), 애플리케이션(Facade), Web 계층 명확히 분리
+ Repository → Domain 변환 과정도 모두 분리됨.

### ✔ InMemory / JPA Repository 분리 설계
+ Test 환경: InMemoryRepository
+ Real 운영 환경: JpaRepository<br />
→ DIP 기반의 확장 가능한 구조

### ✔ 이벤트 기반 설계
+ BoardEventListener
+ CommentEventListener
+ LikeEvent 처리 구조<br />
→ count 증가/감소 로직을 Event 기반으로 모듈화















