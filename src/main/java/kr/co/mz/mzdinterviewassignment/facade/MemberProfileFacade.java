package kr.co.mz.mzdinterviewassignment.facade;

import java.util.List;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import kr.co.mz.mzdinterviewassignment.dto.request.member.CreateMemberRequest;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.CreateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.UpdateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberDetailsResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberInfoResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.profile.ProfileResponse;
import kr.co.mz.mzdinterviewassignment.service.MemberService;
import kr.co.mz.mzdinterviewassignment.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facade 패턴이 뭐고 구현 한 이유는?
 * - 저수준의 인터페이스들을 하나의 고수준의 인터페이스에 묶어 통합 인터페이스를 제공하는 패턴입니다.
 * - 이렇게 구현한 이유는 MemberService와 ProfileService계층에서 MemberRepository와 ProfileRepository를 같이 사용하게 되어서
 * - Service 계층에서 같이 사용해도 될까?, 수 많은 비즈니스 코드를 같이 존재해도 될까? 책임을 분리해야하지 않을까? 라는 고민을 했었고
 * - Service 계층을 의존하게 해볼까? 라는 생각에 순환 참조가 발생할 수 있다는걸 알게 되었고
 * - 그래서 퍼사드 패턴으로 여러 기능을 하나의 트랜잭션 단위에서 동작 가능하도록 구현했습니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
/**
 * 선언적 트랜잭션
 * 읽기 전용 트랜잭션으로 데이터를 상태 변경하지 않고 읽기만 할 때 사용되므로 불필요한 작업을 수행하지 않아 성능이 최적화
 */
@Transactional(readOnly = true)
public class MemberProfileFacade {

    private final MemberService memberService;
    private final ProfileService profileService;

    /**
     * 처음 구현 했을 때는 회원 정보만 받아서 생성하도록 구현했었는데
     * 요구사항에 보면 회원은 최소 1개의 프로필을 가져야 되기 때문에
     * 데이터의 무결성(데이터가 정확하고 일관된 상태)을 위해서 같이 생성하도록 구현했습니다.
     */
    @Transactional
    public MemberResponse createMember(final CreateMemberRequest dto) {
        Member member = memberService.createMember(dto);
        ProfileResponse response = profileService.createProfile(dto.getProfile(), member);
        return MemberResponse.generateMemberResponse(member, response);
    }

    /**
     * 트랜잭션 전파 옵션을 기본으로 설정했기 때문에
     * 외부 트랜잭션과 내부 트랜잭션은 하나의 물리 트랜잭션으로 구성 되어 있어
     * 모든 논리 트랜잭션이 커밋되어야 물리 트랜잭션이 커밋이 되고
     * 한 논리 트랜잭션이 롤백이 되면 물리 트랜잭션은 롤백이 됩니다.
     * 같은 물리 트랜잭션 == 같은 동기화 커넥션 사용
     *
     * 트랜잭션을 분리할꺼면 REQUIRES_NEW 옵션을 사용
     * 옵션
     * - SUPPORT
     * - NOT_SUPPORT
     * - MANDATORY
     * - NEVER
     * - NESTED
     */
    @Transactional
    public String deleteMember(final Long memberNo) {
        return memberService.deleteMember(memberNo);
    }

    public MemberDetailsResponse findMemberDetails(final Long memberNo) {
        Member member = memberService.findMember(memberNo);
        List<ProfileResponse> responses = profileService.findProfiles(member);

        return MemberDetailsResponse.generateMemberDetails(member, responses);
    }

    public List<MemberInfoResponse> findMembers(final int page, final int size, final String name) {
        Page<Member> members = memberService.findMembersContainName(name, page, size);

        return members.stream().map(member -> {
            ProfileResponse response = profileService.findMainProfile(member);
            return MemberInfoResponse.generateMemberInfo(member, response);
        }).toList();
    }

    /**
     * 원자성: 여러 개의 작업을 하나로 묶어 모두 작업이 성공하거나 실패 해야한다.
     * 일관성: 데이터베이스의 상태가 일관 되어야한다
     * 독립성: 모든 트랜잭션은 다른 트랜잭션에서 독립되어야한다
     * 지속성: 트랜잭션에 대한 로그가 남아있어야한다.
     */
    @Transactional
    public ProfileResponse createProfile(final CreateProfileRequest dto, final Long memberNo) {
        Member member = memberService.findMember(memberNo);
        return profileService.createProfile(dto, member);
    }

    @Transactional
    public ProfileResponse updateProfile(final UpdateProfileRequest dto,
                                         final Long profileNo,
                                         final Long memberNo) {
        Member member = memberService.findMember(memberNo);
        return profileService.updateProfile(dto, profileNo, member);
    }

    @Transactional
    public String deleteProfile(final Long profileNo, final Long memberNo) {
        Member member = memberService.findMember(memberNo);
        return profileService.deleteProfile(profileNo, member);
    }
}
