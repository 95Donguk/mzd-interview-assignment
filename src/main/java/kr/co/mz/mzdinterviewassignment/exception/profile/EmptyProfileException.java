package kr.co.mz.mzdinterviewassignment.exception.profile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmptyProfileException extends RuntimeException {
    public EmptyProfileException(final Long memberNo) {
        log.error("회원 식별번호 {} 의 프로필이 없습니다. 프로필을 추가해야 합니다.", memberNo);
    }
}
