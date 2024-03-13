# MZD 2차 인터뷰 과제

### 과제 환경

- JAVA 17
- SpringBoot 3.2.3
- MariaDB
    - 로컬 환경
    - application.proterties 파일에서 컴퓨터의 데이터베이스 로컬 환경에 맞게 설정을 해주셔야 합니다.
    - MariaDB 설정 중에  `default-character-set=utf8` 로 설정 해주시거나
    - MariaDB 콘솔에서 `ALTER TABLE [테이블명] convert to charset utf8;`
        - 테이블명에 `MEMBER_TBL`, `PROFILE_TBL`를 각각 넣어서 명령어를 입력해주세요.
- JPA

---

### ERD
![ERD](https://github.com/95Donguk/mzd-interview-assignment/assets/90694560/a6a8a650-662e-4041-8c1d-0a3f691706a2)

---

### API

|    구분     |    내용     | Method |                                 URI                                 |
|:---------:|:---------:|:------:|:-------------------------------------------------------------------:|
|    회원     | 회원 생성 API |  POST  |                            /api/members                             |
|    회원     | 회원 삭제 API | DELETE |                      /api/members/{member_no}                       |
|    회원     |   회원 전체 조회 API    |  GET   | /api/members?page={page_no}&size={members_count}&name={member_name} |
|    회원     |    회원 상세 조회 API    |  GET   |                      /api/members/{member_no}                       |
|  회원 프로필   |   회원 프로필 생성 API    |  POST  |                  /api/members/{member_no}/profiles                  |
|  회원 프로필   |   회원 프로필 수정 API    | PATCH  |           /api/members/{member_no}/profiles/{profile_no}            |
|  회원 프로필   |    회원 프로필 삭제 API    | DELETE |           /api/members/{member_no}/profiles/{profile_no}            |

---
### API DOCS
#### - 회원 생성 API -

> POST /api/members 

#### Request Body Parameter
```
  {
    "loginId": "honggildong00",
    "name": "홍길동",
    "password": "gildong00!",
    "profile": {
        "nickname": "히어로",
        "phoneNumber": "01012345678",
        "address": "서울특별시 성북구 화랑도 11길 26 103동 1602호 (하월곡동, 한국아파트)"
    }
  }
```

- loginId(필수) 
  - 아이디는 영문 소문자와 숫자로 구성된 4 ~ 12자리로 입력해주세요.              
    
- name(필수)
  - 이름은 한글로 구성된 2 ~ 8자리로 입력해주세요.                   
- password(필수)
  - 비밀번호는 영문 대소문자, 숫자, 특수문자'~!@#$%^&*()+\|='로 구성된 8 ~ 16자리로 입력해주세요.  
- profile(필수)
  - 회원 가입 시 생성할 프로필 입니다.
      - nickname(필수)
        - 프로필의 별명입니다.
        - 닉네임은 영문 대소문자, 숫자, 한글로 구성된 2 ~ 8자리로 입력해주세요.
      - phoneNumber(필수)
          - 프로필의 휴대폰 번호입니다.
          - 휴대전화 번호는 하이픈(-)을 제외한 10자리 또는 11자리로 입력해주세요.
      - address
          - 프로필의 주소입니다.

---
#### Response
```
{
    "code": "CREATED",
    "message": "회원 생성 성공",
    "data": {
        "memberNo": 1,
        "loginId": "honggildong00",
        "name": "홍길동",
        "createdAt": "2024-03-13T17:43:27.8074669",
        "profile": {
            "profileNo": 1,
            "nickname": "히어로",
            "phoneNumber": "01012345678",
            "address": "서울특별시 성북구 화랑도 11길 26 103동 1602호 (하월곡동, 한국아파트)"
            "profileStatus": "MAIN",
            "createdAt": "2024-03-13T17:43:27.8466229",
            "updatedAt": "2024-03-13T17:43:27.8466229"
        }
    }
}
```
- code : http 상태 코드 입니다.


- message : 요청 결과 메시지 입니다.


- data : 생성된 회원 정보 입니다.
  - memberNo : 생성된 회원 식별 번호
  - loginId : 생성된 회원 로그인 아이디
  - name : 생성된 회원 이름
  - createdAt : 회원 생성 시간
  - profile : 생성된 프로필 정보
    - profileNo : 생성된 프로필 식별 번호
    - nickname : 생성된 프로필 별명
    - phoneNumber : 생성된 프로필 휴대전화번호
    - address : 생성된 프로필 주소
    - profileStatus : 프로필 상태(MAIN: 메인프로필, NORMAL: 일반 프로필)
    - createdAt : 프로필 생성 시간
    - updatedAt : 프로필 수정 시간

---

#### - 회원 삭제 API -

> DELETE /api/members/{member_no}

#### Response
```
{
    "code": "OK",
    "message": "회원 삭제 성공",
    "data": "삭제된 회원 아이디 : honggildong00"
}
```
- code : http 상태 코드 입니다.


- message : 요청 결과 메시지 입니다.


- data : 삭제 요청한 회원 아이디를 출력합니다.

---

#### - 회원 전체 조회 API -

> GET /api/members?page={page_no}&size={members_count}&name={member_name}

#### Response
```
{
    "code": "OK",
    "message": "회원 전체 조회 성공",
    "data": [
        {
            "memberNo": 1,
            "loginId": "honggildong00",
            "name": "홍길동",
            "password": "1q2w3e4r5t",
            "memberStatus": "ACTIVE",
            "createdAt": "2024-03-13T17:43:27.8074669",
            "updatedAt": "2024-03-13T17:43:27.8074669",
            "mainProfile": {
                "profileNo": 1,
                "nickname": "히어로",
                "phoneNumber": "01012345678",
                "address": "서울특별시 성북구 화랑도 11길 26 103동 1602호 (하월곡동, 한국아파트)"
                "profileStatus": "MAIN",
                "createdAt": "2024-03-13T17:43:27.8466229",
                "updatedAt": "2024-03-13T17:43:27.8466229"
            }
        },
        {
            "memberNo": 5,
            "loginId": "lovelee2",
            "name": "이몽룡",
            "password": "1q2w3e4r5t",
            "memberStatus": "ACTIVE",
            "updatedAt": "2024-03-13T18:31:11.637024",
            "createdAt": "2024-03-13T18:31:11.637024",
            "mainProfile": {
                "profileNo": 9,
                "nickname": "사랑꾼",
                "phoneNumber": "01056781234",
                "address": "사랑시 고백구 행복동 7-12",
                "profileStatus": "MAIN",
                "createdAt": "2024-03-13T19:56:27.303917",
                "updatedAt": "2024-03-13T19:59:54.323729"
            }
        }
    ]
}
```
- code : http 상태 코드 입니다.


- message : 요청 결과 메시지 입니다.


- data : 전체 회원 정보
    - memberNo : 회원 식별 번호
    - loginId : 회원 로그인 아이디
    - name : 회원 이름
    - password : 회원 비밀번호
    - memberStatus : 회원 상태(ACTIVE: 활성, DELETED: 삭제)
    - createdAt : 회원 생성 시간
    - updatedAt : 회원 수정 시간
    - mainProfile : 메인 프로필 정보
        - profileNo : 프로필 식별 번호
        - nickname : 프로필 별명
        - phoneNumber : 프로필 휴대전화번호
        - address : 프로필 주소
        - profileStatus : 프로필 상태(MAIN: 메인프로필, NORMAL: 일반 프로필)
        - createdAt : 프로필 생성 시간
        - updatedAt : 프로필 수정 시간

---
#### - 회원 상세 조회 API -

> GET /api/members/{member_no}

#### Response
```
{
    "code": "OK",
    "message": "회원 상세 조회 성공",
    "data": {
        "memberNo": 1,
        "loginId": "honggildong00",
        "name": "홍길동",
        "password": "1q2w3e4r5t",
        "memberStatus": "ACTIVE",
        "createdAt": "2024-03-13T17:43:27.8074669",
        "updatedAt": "2024-03-13T17:43:27.8074669",
        "profiles": [
            {
                "profileNo": 1,
                "nickname": "히어로",
                "phoneNumber": "01012345678",
                "address": "서울특별시 성북구 화랑도 11길 26 103동 1602호 (하월곡동, 한국아파트)"
                "profileStatus": "MAIN",
                "createdAt": "2024-03-13T17:43:27.8466229",
                "updatedAt": "2024-03-13T17:43:27.8466229"
            },
            {
                "profileNo": 18,
                "nickname": "쾌도홍길동",
                "phoneNumber": "01011118888",
                "address": 경상남도 창원시 의창구 사림동 1,
                "profileStatus": "NORMAL",
                "createdAt": "2024-03-13T20:11:26.824097",
                "updatedAt": "2024-03-13T20:11:26.824097"
            },
            {
                "profileNo": 25,
                "nickname": "의적",
                "phoneNumber": "01012345678",
                "address": 부산광역시 연제구 중앙대로 1001,
                "profileStatus": "NORMAL",
                "createdAt": "2024-03-13T20:49:38.186319",
                "updatedAt": "2024-03-13T20:49:38.186319"
            },
        ]
    }
}
```
- code : http 상태 코드 입니다.


- message : 요청 결과 메시지 입니다.


- data : 전체 회원 정보
    - memberNo : 회원 식별 번호
    - loginId : 회원 로그인 아이디
    - name : 회원 이름
    - password : 회원 비밀번호
    - memberStatus : 회원 상태(ACTIVE: 활성, DELETED: 삭제)
    - createdAt : 회원 생성 시간
    - updatedAt : 회원 수정 시간
    - profiles : 회원 전체 프로필 정보
        - profileNo : 프로필 식별 번호
        - nickname : 프로필 별명
        - phoneNumber : 프로필 휴대전화번호
        - address : 프로필 주소
        - profileStatus : 프로필 상태(MAIN: 메인프로필, NORMAL: 일반 프로필)
        - createdAt : 프로필 생성 시간
        - updatedAt : 프로필 수정 시간
---
#### - 회원 프로필 생성 API -

> POST /api/members/{member_no}/profiles

#### Request Body Parameter
```
  {
      "nickname": "히어로",
      "phoneNumber": "01012345678",
      "address": "서울특별시 성북구 화랑도 11길 26 103동 1602호 (하월곡동, 한국아파트)"
  }
```
- nickname(필수)
  - 프로필의 별명입니다.
  - 닉네임은 영문 대소문자, 숫자, 한글로 구성된 2 ~ 8자리로 입력해주세요.


- phoneNumber(필수)
  - 프로필의 휴대폰 번호입니다. 
  - 휴대전화 번호는 하이픈(-)을 제외한 10자리 또는 11자리로 입력해주세요.


- address
  - 프로필의 주소입니다.

---
#### Response
```
{
    "code": "CREATED",
    "message": "회원 프로필 생성 성공",
    "data": {
        "profileNo": 18,
        "nickname": "쾌도홍길동",
        "phoneNumber": "01011118888",
        "address": 경상남도 창원시 의창구 사림동 1,
        "profileStatus": "NORMAL",
        "createdAt": "2024-03-13T20:11:26.824097",
        "updatedAt": "2024-03-13T20:11:26.824097"
    }
}
```
- code : http 상태 코드 입니다.


- message : 요청 결과 메시지 입니다.


- data : 생성된 프로필 정보 입니다.
  - profileNo : 생성된 프로필 식별 번호
  - nickname : 생성된 프로필 별명
  - phoneNumber : 생성된 프로필 휴대전화번호
  - address : 생성된 프로필 주소
  - profileStatus : 프로필 상태(MAIN: 메인프로필, NORMAL: 일반 프로필)
  - createdAt : 프로필 생성 시간
  - updatedAt : 프로필 수정 시간
---
#### - 회원 프로필 수정 API -

> PATCH /api/members/{member_no}/profiles/{profile_no}

#### Request Body Parameter
```
{
  "nickname": "사랑꾼",
  "phoneNumber": "01056781234",
  "address": "사랑시 고백구 행복동 7-12",
  "profileStatus": "MAIN"
}
```
- nickname(필수)
    - 프로필의 별명입니다.
    - 닉네임은 영문 대소문자, 숫자, 한글로 구성된 2 ~ 8자리로 입력해주세요.


- phoneNumber(필수)
    - 프로필의 휴대폰 번호입니다.
    - 휴대전화 번호는 하이픈(-)을 제외한 10자리 또는 11자리로 입력해주세요.


- address
    - 프로필의 주소입니다.


- profileStatus(필수)
    - 프로필의 상태입니다.
    - 상태는 MAIN, NORMAL 만 취급합니다.
      - MAIN: 메인프로필, NORMAL: 일반 프로필

---
#### Response
```
{
    "code": "OK",
    "message": "회원 프로필 수정 성공",
    "data": {
        "profileNo": 9,
        "nickname": "사랑꾼",
        "phoneNumber": "01056781234",
        "address": "사랑시 고백구 행복동 7-12",
        "profileStatus": "MAIN",
        "createdAt": "2024-03-13T19:56:27.303917",
        "updatedAt": "2024-03-13T19:59:54.323729"
    }
}
```
- code : http 상태 코드 입니다.


- message : 요청 결과 메시지 입니다.


- data : 수정된 프로필 정보 입니다.
    - profileNo : 수정된 프로필 식별 번호
    - nickname : 수정된 프로필 별명
    - phoneNumber : 수정된 프로필 휴대전화번호
    - address : 수정된 프로필 주소
    - profileStatus : 프로필 상태(MAIN: 메인프로필, NORMAL: 일반 프로필)
    - createdAt : 프로필 생성 시간
    - updatedAt : 프로필 수정 시간
---
#### - 회원 삭제 API -

> DELETE /api/members/{member_no}/profiles/{profile_no}

#### Response
```
{
    "code": "OK",
    "message": "회원 프로필 삭제 성공",
    "data": "삭제된 프로필 닉네임 : 의적"
}
```
- code : http 상태 코드 입니다.


- message : 요청 결과 메시지 입니다.


- data : 삭제 요청한 프로필 닉네임을 출력합니다.

---


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


