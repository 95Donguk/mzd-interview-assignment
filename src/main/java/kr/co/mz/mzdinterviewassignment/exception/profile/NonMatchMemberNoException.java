package kr.co.mz.mzdinterviewassignment.exception.profile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NonMatchMemberNoException extends RuntimeException {
    public NonMatchMemberNoException(final Long memberNo, final Long profileMemberNo) {
        super("회원과 프로필이 일치 하지 않습니다.");
        log.error("회원 식별 번호와 프로필의 회원 식별 번호가 다릅니다. 회원 식별 번호 :{}, 프로필의 회원 식별 번호 : {}", memberNo,
            profileMemberNo);
    }
}
