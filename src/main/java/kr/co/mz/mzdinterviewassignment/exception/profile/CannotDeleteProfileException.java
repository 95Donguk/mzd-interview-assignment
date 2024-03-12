package kr.co.mz.mzdinterviewassignment.exception.profile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CannotDeleteProfileException extends RuntimeException {
    public CannotDeleteProfileException(final String loginId) {
        super(loginId + "님의 프로필은 최소 1개의 프로필이 있어야하므로 프로필을 삭제 할 수 없습니다.");
        log.info("{} 님의 프로필은 최소 1개의 프로필이 있어야하므로 프로필을 삭제 할 수 없습니다.", loginId);
    }
}
