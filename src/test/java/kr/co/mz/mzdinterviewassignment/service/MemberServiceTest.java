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
import kr.co.mz.mzdinterviewassignment.dto.request.profile.CreateProfileRequest;
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

        CreateProfileRequest profileDto = new CreateProfileRequest("홍길동", "01098765432",
            "서울특별시 종로구 청계천로 85 17층(관철동, 삼일빌딩) 한국지역정보개발원");

        CreateMemberRequest dto =
            new CreateMemberRequest("testid", "테스트", "testPassword", profileDto);

        Mockito.when(memberRepository.findByLoginId(dto.getLoginId()))
            .thenReturn(Optional.of(member));

        assertThatThrownBy(() -> memberService.createMember(dto))
            .isInstanceOf(DuplicateLoginIdException.class);

        Mockito.verify(memberRepository, Mockito.never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 생성 성공 테스트")
    void createMember_Success_Test() {

        CreateProfileRequest profileDto = new CreateProfileRequest("홍길동", "01098765432",
            "서울특별시 종로구 청계천로 85 17층(관철동, 삼일빌딩) 한국지역정보개발원");

        CreateMemberRequest dto =
            new CreateMemberRequest("testid", "테스트", "testPassword", profileDto);

        Mockito.when(memberRepository.save(any(Member.class)))
            .then(returnsFirstArg());

        Member member = memberService.createMember(dto);

        assertThat(member.getLoginId()).isEqualTo(dto.getLoginId());
        assertThat(member.getName()).isEqualTo(dto.getName());

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

        assertThat(loginId).isEqualTo("testid");
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

        assertThat(findMember.getLoginId()).isEqualTo("testid");
        assertThat(findMember.getName()).isEqualTo("테스트");
        assertThat(findMember.getPassword()).isEqualTo("testPassword");

        Mockito.verify(memberRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("이름 검색 키워드 회원 전체 조회")
    void findMembersByNameContaining_Test() {
        Page<Member> members = generatePageMembers();

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "memberNo"));

        Mockito.when(memberRepository.findMembersByNameContaining("테스트", pageRequest))
            .thenReturn(members);
        Page<Member> result = memberService.findMembersContainName("테스트", 0, 10);

        assertThat(result.getContent()).hasSize(10);
        Mockito.verify(memberRepository, Mockito.times(1))
            .findMembersByNameContaining("테스트", pageRequest);
    }

    private static PageImpl<Member> generatePageMembers() {
        return new PageImpl<>(IntStream.range(1, 11)
            .mapToObj(i -> Member.builder()
                .loginId("testid" + i)
                .name("테스트")
                .password("testPassword" + i)
                .build())
            .toList());
    }

    private static Member generateMember() {
        return Member.builder()
            .loginId("testid")
            .name("테스트")
            .password("testPassword")
            .build();
    }
}