package kr.co.mz.mzdinterviewassignment.service;

import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import kr.co.mz.mzdinterviewassignment.dto.request.member.CreateMemberRequest;
import kr.co.mz.mzdinterviewassignment.exception.member.DuplicateLoginIdException;
import kr.co.mz.mzdinterviewassignment.exception.member.NotFoundMemberException;
import kr.co.mz.mzdinterviewassignment.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member createMember(final CreateMemberRequest dto) {
        log.info("회원 생성 시작");

        if (hasDuplicateLoginId(dto.getLoginId())) {
            throw new DuplicateLoginIdException(dto.getLoginId());
        }

        Member member = memberRepository.save(dto.toEntity());
        log.info("회원 생성 성공");
        return member;
    }

    private boolean hasDuplicateLoginId(final String loginId) {
        log.info("중복된 아이디가 있는지 확인");
        return memberRepository.findByLoginId(loginId).isPresent();
    }

    @Transactional
    public String deleteMember(final Long memberNo) {
        log.info("회원 삭제 시작");

        Member member = memberRepository.findById(memberNo)
            .orElseThrow(() -> new NotFoundMemberException(memberNo));

        log.info("회원 삭제 성공");
        return member.delete();
    }

    public Member findMember(final Long memberNo) {
        log.info("회원 식별번호 {} 의 정보 조회", memberNo);
        return memberRepository.findById(memberNo)
            .orElseThrow(() -> new NotFoundMemberException(memberNo));
    }

    public Page<Member> findMembersContainName(final String name,
                                               final int page,
                                               final int size) {
        log.info("이름에 {} 들어간 회원 전체 조회", name);

        PageRequest pageRequest =
            PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "memberNo"));

        return memberRepository.findMembersByNameContaining(name, pageRequest);
    }
}
