package kr.co.mz.mzdinterviewassignment.exception.profile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundProfileException extends RuntimeException {
    public NotFoundProfileException(final Long profileNo) {
        log.error("프로필을 찾을 수 없습니다. 프로필 식별 번호 : {}", profileNo);
    }
}
