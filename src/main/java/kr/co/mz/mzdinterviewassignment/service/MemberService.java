package kr.co.mz.mzdinterviewassignment.service;

import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import kr.co.mz.mzdinterviewassignment.dto.request.member.CreateMemberRequest;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberResponse;
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
    public MemberResponse createMember(final CreateMemberRequest dto) {

        if (hasDuplicateLoginId(dto.getLoginId())) {
            throw new DuplicateLoginIdException(dto.getLoginId());
        }

        Member member = memberRepository.save(dto.toEntity());
        return MemberResponse.generateMemberResponse(member);
    }

    private boolean hasDuplicateLoginId(final String loginId) {
        return memberRepository.findByLoginId(loginId).isPresent();
    }

    @Transactional
    public String deleteMember(final Long memberNo) {
        Member member = memberRepository.findById(memberNo)
            .orElseThrow(() -> new NotFoundMemberException(memberNo));

        return member.delete();
    }

    public Member findMember(final Long memberNo) {
        log.info("회원 식별번호 {} 의 정보 조회", memberNo);
        return memberRepository.findById(memberNo)
            .orElseThrow(() -> new NotFoundMemberException(memberNo));
    }

    public Page<Member> findMembers(final int page, final int size) {
        log.info("회원 전체 조회");

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "memberNo"));

        return memberRepository.findAll(pageRequest);
    }

    public Page<Member> findMembersContainName(final String name,
                                               final int page,
                                               final int size) {
        log.info("이름이 {} 들어간 회원 전체 조회");

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "memberNo"));

        return memberRepository.findMembersByNameContaining(name, pageRequest);
    }
}
