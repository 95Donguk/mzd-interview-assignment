package kr.co.mz.mzdinterviewassignment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import kr.co.mz.mzdinterviewassignment.domain.member.MemberStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("데이터베이스에 존재하지 않은 loginId로 회원 조회 테스트 ")
    void findByLoginId_HasNotLoginId_Test() {

        Member member = generateMember();

        memberRepository.saveAndFlush(member);

        assertThat(memberRepository.findByLoginId("testId2")).isEmpty();
    }

    @Test
    @DisplayName("논리 삭제된 회원의 loginId 조회 테스트")
    void findByLoginId_DeletedMember_Test() {

        Member member = generateMember();

        Member savedMember = memberRepository.saveAndFlush(member);

        savedMember.delete();

        assertThat(memberRepository.findByLoginId("testId")).isEmpty();
    }

    @Test
    @DisplayName("loginId로 회원 조회 테스트 ")
    void findByLoginId_Test() {

        Member member = generateMember();

        memberRepository.saveAndFlush(member);

        Member findMember = memberRepository.findByLoginId("testId").orElseThrow();

        assertThat(findMember.getLoginId()).isEqualTo("testId");
        assertThat(findMember.getName()).isEqualTo("testName");
        assertThat(findMember.getPassword()).isEqualTo("testPassword");
        assertThat(findMember.getMemberStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("데이터베이스에 존재하지 않은 이름 키워드로 전체 회원 조회")
    void findMembersByNameContaining_HasNotName_Test() {

        List<Member> members = generateMembers();

        memberRepository.saveAllAndFlush(members);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "memberNo"));

        Page<Member> findMembers = memberRepository.findMembersByNameContaining("테스트", pageRequest);

        assertThat(findMembers.getContent()).isEmpty();
    }

    @Test
    @DisplayName("데이터베이스에 논리 삭제된 회원의 이름 키워드로 전체 회원 조회")
    void findMembersByNameContaining_DeletedMember_Test() {

        List<Member> members = generateMembers();

        List<Member> savedMembers = memberRepository.saveAllAndFlush(members);

        savedMembers.forEach(Member::delete);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "memberNo"));

        Page<Member> findMembers =
            memberRepository.findMembersByNameContaining("testName", pageRequest);

        assertThat(findMembers.getContent()).isEmpty();
    }

    @Test
    @DisplayName("이름 키워드로 전체 회원 조회")
    void findMembersByNameContaining_Test() {

        List<Member> members = generateMembers();

        memberRepository.saveAllAndFlush(members);

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "memberNo"));

        Page<Member> findMembers =
            memberRepository.findMembersByNameContaining("testName", pageRequest);

        findMembers.forEach(member -> assertThat(member.getName()).contains("testName"));
        assertThat(findMembers.getContent()).hasSize(10);
    }

    private static Member generateMember() {
        return Member.builder()
            .loginId("testId")
            .name("testName")
            .password("testPassword")
            .build();
    }

    private static List<Member> generateMembers() {
        return IntStream.range(0, 30)
            .mapToObj(i -> Member.builder()
                .loginId("testId" + i)
                .name("testName" + i)
                .password("testPassword" + i)
                .build())
            .toList();
    }
}