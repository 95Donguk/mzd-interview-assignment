package kr.co.mz.mzdinterviewassignment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

import java.util.Optional;
import java.util.stream.IntStream;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import kr.co.mz.mzdinterviewassignment.domain.member.MemberStatus;
import kr.co.mz.mzdinterviewassignment.dto.request.member.CreateMemberRequest;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberResponse;
import kr.co.mz.mzdinterviewassignment.exception.member.DuplicateLoginIdException;
import kr.co.mz.mzdinterviewassignment.exception.member.NotFoundMemberException;
import kr.co.mz.mzdinterviewassignment.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

class MemberServiceTest {

    private final MemberRepository memberRepository = Mockito.mock(MemberRepository.class);
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberService(memberRepository);
    }

    @Test
    @DisplayName("이미 중복된 아이디가 있을 시 회원 생성 실패 테스트")
    void createMember_DuplicateLoginId_Fail_Test() {

        Member member = generateMember();

        CreateMemberRequest dto = new CreateMemberRequest("testId", "testName", "testPassword");

        Mockito.when(memberRepository.findByLoginId(dto.getLoginId()))
            .thenReturn(Optional.of(member));

        assertThatThrownBy(() -> memberService.createMember(dto))
            .isInstanceOf(DuplicateLoginIdException.class);

        Mockito.verify(memberRepository, Mockito.never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 생성 성공 테스트")
    void createMember_Success_Test() {

        CreateMemberRequest dto = new CreateMemberRequest("testId", "testName", "testPassword");

        Mockito.when(memberRepository.save(any(Member.class)))
            .then(returnsFirstArg());

        MemberResponse member = memberService.createMember(dto);

        assertThat(member.getLoginId()).isEqualTo("testId");
        assertThat(member.getName()).isEqualTo("testName");

        Mockito.verify(memberRepository, Mockito.times(1)).save(any(Member.class));
    }


    @Test
    @DisplayName("회원 식별 번호로 회원을 찾지 못할 시 회원 삭제 실패 테스트")
    void deleteMember_NotFoundMember_Fail_Test() {

        Mockito.when(memberRepository.findById(1L))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.deleteMember(1L))
            .isInstanceOf(NotFoundMemberException.class);

        Mockito.verify(memberRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 삭제 성공 테스트")
    void deleteMember_Success_Test() {

        Member member = generateMember();

        Mockito.when(memberRepository.findById(1L))
            .thenReturn(Optional.of(member));

        String loginId = memberService.deleteMember(1L);

        assertThat(loginId).isEqualTo("testId");
        assertThat(member.getMemberStatus()).isEqualTo(MemberStatus.DELETED);

        Mockito.verify(memberRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 식별 번호로 회원을 찾지 못할 시 회원 조회 실패 테스트")
    void findMember_NotFoundMember_Fail_Test() {

        Mockito.when(memberRepository.findById(1L))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.findMember(1L))
            .isInstanceOf(NotFoundMemberException.class);

        Mockito.verify(memberRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("회원 조회 성공 테스트")
    void findMember_Success_Test() {

        Member member = generateMember();

        Mockito.when(memberRepository.findById(1L))
            .thenReturn(Optional.of(member));

        Member findMember = memberService.findMember(1L);

        assertThat(findMember.getLoginId()).isEqualTo("testId");
        assertThat(findMember.getName()).isEqualTo("testName");
        assertThat(findMember.getPassword()).isEqualTo("testPassword");

        Mockito.verify(memberRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("이름 검색 키워드 회원 전체 조회")
    void findMembersByNameContaining_Test() {
        Page<Member> members = generatePageMembers();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "memberNo"));

        Mockito.when(memberRepository.findMembersByNameContaining("testName", pageRequest))
            .thenReturn(members);
        Page<Member> result = memberService.findMembersContainName("testName", 0, 10);

        assertThat(result.getContent()).hasSize(10);
        Mockito.verify(memberRepository, Mockito.times(1))
            .findMembersByNameContaining("testName", pageRequest);
    }

    private static PageImpl<Member> generatePageMembers() {
        return new PageImpl<>(IntStream.range(1, 11)
            .mapToObj(i -> Member.builder()
                .loginId("testId" + i)
                .name("testName" + i)
                .password("testPassword" + i)
                .build())
            .toList());
    }

    private static Member generateMember() {
        return Member.builder()
            .loginId("testId")
            .name("testName")
            .password("testPassword")
            .build();
    }
}