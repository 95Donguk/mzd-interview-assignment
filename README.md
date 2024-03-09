# MZD 2차 인터뷰 과제

### API

|    구분     |    내용     |        Method        |                                 URI                                 |
|:---------:|:---------:|:--------------------:|:-------------------------------------------------------------------:|
|    회원     | 회원 생성 API |    POST    |                            /api/members                             |
|    회원     | 회원 삭제 API |      DELETE      |                      /api/members/{member_no}                       |
|    회원     |   회원 전체 조회 API    | GET | /api/members?page={page_no}&size={members_count}&name={member_name} |
|    회원     |    회원 상세 조회 API    |    GET     |                      /api/members/{member_no}                       |
|  회원 프로필   |   회원 프로필 생성 API    |      POST       |                  /api/members/{member_no}/profiles                  |
|  회원 프로필   |   회원 프로필 수정 API    |    PUT    |           /api/members/{member_no}/profiles/{profile_no}            |
|  회원 프로필   |    회원 프로필 삭제 API    |    DELETE    |           /api/members/{member_no}/profiles/{profile_no}            |

### ERD
![ERD](https://github.com/95Donguk/mzd-interview-assignment/assets/90694560/c17cab7c-887c-46a0-9bbe-7f251c2a2d71)
### 과제 환경

- JAVA 17
- SpringBoot 3.2.3
- MariaDB
- JPA

### 요구 사항

- 회원 관리 시스템 (필수 요건, 추가적인 요건은 자유롭게 설정하셔도 됩니다.)

  - 회원은 Login ID, 이름, PW를 갖는다.

  - 회원은 여러 개의 프로필을 가질 수 있다.

    - 회원은 최소 1개의 프로필을 가지며, 메인 프로필 정보를 가진다.

  - 프로필 정보는 별명, 휴대폰 번호, 주소를 포함한다.

    - 별명, 휴대폰 번호는 필수 값이다.

1. 아래 REST API를 구현하시오.

   - 회원 생성 API

    - 회원 프로필 생성 API

    - 회원 프로필 수정 API

    - 회원 삭제 API

    - 회원 프로필 삭제 API

    - 회원 전체 조회 API

      - 전체 조회 시, 회원의 메인 프로필 정보를 포함한다.

      - 페이지네이션을 적용한다. 한 번에 가져올 개수를 요청 파라미터에 입력받을 수 있도록 한다.

      - 요청 파라미터로 이름을 입력할 시, 해당 이름으로 필터링하여 조회한다.

   - 상세 조회 API

     - 회원 PK key로 조회한다.

     - 회원 정보와 해당 회원의 모든 프로필을 반환한다.


2. JUnit 기반 Test code를 작성하시오.


