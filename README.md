# BlogService

> **대규모 트래픽과 1,200만 건 이상의 대용량 데이터를 고려한 고성능 블로그 백엔드 시스템**
>
> 단순한 CRUD 기능을 넘어, 데이터 급증 시 발생하는 **DB 병목 현상을 진단하고 아키텍처 및 쿼리 수준에서 최적화**하는 과정을 담았습니다.

---

## 기술 스택

| Category                 | Technology                                            |
|:-------------------------|:------------------------------------------------------|
| **Language & Framework** | **Java 21**, **Spring Boot 3.5.8**, Spring Security 6 |
| **Persistence**          | **Spring Data JPA**, QueryDSL, **MariaDB**            |
| **Caching & Session**    | **Redis**                                             |
| **Security**             | **JWT** (Access/Refresh)                              |
| **Performance Test**     | **Artillery** (Load Testing)                          |

---
## 상세 기능

### 인증 및 보안
* **회원가입 (Sign-up)**
    - 이메일(ID), 이름, 닉네임, 비밀번호, 생년월일, 휴대폰번호, 주소 수집.
    - **유효성 검증:** 이메일/닉네임 중복 불가, 닉네임(3~10자), 비밀번호(8~20자).
    - **보안:** `DelegatingPasswordEncoder`를 통한 비밀번호 암호화.
    - **응답:** 가입 성공 시 유저 DB 고유 ID 반환.
* **로그인 및 로그아웃 (Login/Logout)**
    - **JWT 시스템:** 로그인 시 Access/Refresh Token 발급 및 `ResponseCookie`에 저장.
    - **로그인 기록:** 접속 시마다 로그인 기록 테이블에 데이터 로깅.
    - **보안 로그아웃:** 로그아웃 시 토큰 블랙리스트 등록 및 쿠키 즉시 제거.
* **토큰 재발급 (Token Refresh)**
    - Refresh Token을 검증하여 안전하게 Access Token 재발급.

### 회원 정보 관리
* **정보 조회/수정:** 이름, 닉네임, 이메일, 생년월일, 번호, 주소 조회 및 일부 필드(닉네임, 번호, 주소) 수정.
* **비밀번호 변경:** 기존 비밀번호 인증 절차를 거친 후 새로운 비밀번호로 변경.
* **회원 탈퇴 (Resign):** 비밀번호 인증 후 처리되며, 데이터 물리 삭제 대신 **탈퇴 필드 체크(Soft Delete)** 방식을 채택하여 데이터 무결성 유지.

### 게시글 관리
* **작성/수정/삭제:** 인증된 사용자만 가능하며, 모든 변경 시점에 **데이터 스냅샷**을 기록하여 이력 관리.
* **조회 서비스:** - **단건 조회:** 작성자 닉네임, 시간, 조회수, 좋아요 수 등 상세 정보 표시.
    - **목록 조회:** 페이지당 10개씩 고성능 페이징 처리.
* **스마트 조회수 카운팅:** 과거 조회 여부를 판단하여 **사용자당 최초 1회만** 조회수를 증가시키는 로직 적용.
* **좋아요 기능:** 게시글당 1회 토글(Toggle) 방식. (클릭 시 +1, 재클릭 시 취소 및 -1)

### 댓글 및 대댓글
* **계층형 구조:** 댓글뿐만 아니라 대댓글(Nested Comment) 작성을 지원하여 커뮤니티 기능 강화.
* **권한 및 무결성:**
    - 인증된 사용자만 작성/수정/삭제 가능.
    - **스냅샷 시스템:** 댓글 작성/수정/삭제 시점에 스냅샷을 생성하여 변경 이력 보존.
    - **Soft Delete:** 댓글 삭제 시 데이터를 물리적으로 지우지 않고 삭제 필드 표시.

---

## Performance Optimization

본 프로젝트는 1,200만 건의 더미 데이터를 기반으로 실제 운영 환경에서 발생할 수 있는 성능 문제를 해결했습니다.

### 페이징 성능 86배 단축 (Covering Index & Deferred Join)
* **Problem:** 1,200만 건 데이터 환경에서 `Offset` 기반 페이징 시, 뒷부분 페이지를 조회할수록 읽기 성능이 기하급수적으로 저하됨.
* **Solution:** * **Covering Index:** 데이터 블록 접근 전 인덱스만으로 쿼리를 완성하여 디스크 I/O 절감.
    * **Deferred Join:** PK를 커버링 인덱스로 먼저 추출한 뒤 필요한 시점에만 `JOIN`을 수행하는 지연 조인 전략 적용.
* **Result:** 평균 응답 시간 약 **12,000ms → 140ms (86배 단축)**

### Redis Write-Back을 통한 Write 부하 분산
* **Problem:** 게시글 조회수 및 좋아요 업데이트 시 RDBMS에 직접 Write를 수행할 경우, 트래픽 집중 시 평균 응답 시간 5.3s 및 **에러율 21%** 발생.
* **Solution:** **Redis Write-Back** 패턴 도입. 빈번한 쓰기 요청을 Redis 캐시에서 처리하고, Scheduled 배치를 통해 DB에 일괄 반영.
* **Result:** 평균 응답 시간 **5.3s → 2.4ms**, **에러율 0%** 달성으로 시스템 안정성 확보.

### 대용량 테이블 COUNT 쿼리 최적화
* **Problem:** 전체 게시글 수를 조회하는 `COUNT(*)` 쿼리가 1,200만 건 환경에서 약 5.4s의 지연 발생.
* **Solution:** 데이터 비정규화 및 별도의 통계 테이블 운영, 적절한 락(Lock) 메커니즘을 통한 동기화 처리.
* **Result:** 응답 속도 **5.4s → 2.6ms**로 개선.

---

##  Key Features

### Authentication & Security
- **JWT 기반 인증:** Access/Refresh Token 구조. Refresh Token은 Redis를 활용한 블랙리스트 처리 및 `HttpOnly` 쿠키 적용.
- **회원 관리:** 중복 체크(이메일, 닉네임), 프로필 수정, Soft Delete 기반의 회원 탈퇴 시스템.
- **보안 스냅샷:** 데이터 변경 이력 추적을 위한 `MemberSnapshot` 관리.

### Content Management (Data Integrity)
- **Snapshot Architecture:** 게시글 및 댓글 수정/삭제 시 원본 데이터를 보존하는 스냅샷 테이블을 운영하여 무결성 유지.
- **Smart Views:** 쿠키 및 사용자 정보를 기반으로 과거 조회 여부를 판단하여 **최초 1회만 조회수 증가** 로직 구현.
- **Comment System:** 무한 뎁스 대댓글 지원 및 삭제 시 Soft Delete 처리.

---

## Performance Results (Artillery Test)

| Metric                 | Before Optimization | After Optimization   | Improvement        |
|:-----------------------|:--------------------|:---------------------|:-------------------|
| **Paging (12M rows)**  | ~ 12,000ms          | **~ 140ms**          | **86x Faster**     |
| **Post Views (Write)** | 5,300ms (Error 21%) | **2.4ms (Error 0%)** | **High Stability** |
| **Count Query**        | 5,400ms             | **2.6ms**            | **99.9% Reduced**  |

---


### Installation
```bash
git clone [https://github.com/your-username/BlogService.git](https://github.com/your-username/BlogService.git)
cd BlogService
./gradlew bootRun