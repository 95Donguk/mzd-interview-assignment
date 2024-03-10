package kr.co.mz.mzdinterviewassignment.exception.status;

import lombok.Getter;

@Getter
public enum StatusExceptionCode {
    INVALID_MEMBER("유효하지 않은 회원 상태입니다."),
    INVALID_PROFILE("유효하지 않은 프로필 상태입니다.");

    private final String message;

    StatusExceptionCode(String message) {
        this.message = message;
    }
}
