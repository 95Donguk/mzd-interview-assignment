package kr.co.mz.mzdinterviewassignment.exception.member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DuplicateLoginIdException extends RuntimeException {
    public DuplicateLoginIdException(final String loginId) {
        super("이미 존재하는 아이디입니다.");
        log.error("이미 존재하는 아이디입니다. 아이디: {}", loginId);
    }
}
