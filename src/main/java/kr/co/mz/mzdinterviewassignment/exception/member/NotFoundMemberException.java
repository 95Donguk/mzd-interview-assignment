package kr.co.mz.mzdinterviewassignment.exception.member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundMemberException extends RuntimeException {
    public NotFoundMemberException(final Long memberNo) {
        log.error("회원을 찾을 수 없습니다. 회원 식별 번호 : {}", memberNo);
    }
}
