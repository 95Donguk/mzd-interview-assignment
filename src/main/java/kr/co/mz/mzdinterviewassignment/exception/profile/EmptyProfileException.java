package kr.co.mz.mzdinterviewassignment.exception.profile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmptyProfileException extends RuntimeException {
    public EmptyProfileException(final String loginId) {
        super(loginId + " 회원의 프로필이 없습니다, 프로필을 추가해야 합니다.");
        log.error(" {} 회원의 프로필이 없습니다. 프로필을 추가해야 합니다.", loginId);
    }
}
