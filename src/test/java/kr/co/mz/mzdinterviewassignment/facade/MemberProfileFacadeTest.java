package kr.co.mz.mzdinterviewassignment.facade;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import kr.co.mz.mzdinterviewassignment.domain.profile.ProfileStatus;
import kr.co.mz.mzdinterviewassignment.dto.request.member.CreateMemberRequest;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.CreateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.UpdateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberDetailsResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberInfoResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.profile.ProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * 저수준의 인터페이스들을 하나의 고수준의 인터페이스에 묶어 통합 인터페이스를 제공하는 계층이기 때문에
 * 통합테스트로 구현 했습니다.
 */
@SpringBootTest
/**
 * 테스트가 완료 되면 자동으로 데이터베이스가 롤백이 되도록 합니다.
 */
@Transactional
/**
 * 테스트용 데이터베이스로 테스트를 작업하기 위해서 선언했습니다.
 */
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class MemberProfileFacadeTest {

    @Autowired
    MemberProfileFacade memberProfileFacade;

    private CreateMemberRequest request;

    @BeforeEach
    void setUp() {
        CreateProfileRequest profileDto = new CreateProfileRequest("홍길동", "01098765432",
            "서울특별시 종로구 청계천로 85 17층(관철동, 삼일빌딩) 한국지역정보개발원");

        request = new CreateMemberRequest(
            "test1",
            "테스트",
            "test123@",
            profileDto
        );
    }

    @Test
    @DisplayName("회원 등록 테스트")
    void createMember_Success_Test() {

        MemberResponse member = memberProfileFacade.createMember(request);

        assertThat(member.getMemberNo()).isPositive();
        assertThat(member.getLoginId()).isEqualTo(request.getLoginId());
        assertThat(member.getName()).isEqualTo(request.getName());
        assertThat(member.getProfile().getNickname()).isEqualTo(request.getProfile().getNickname());
        assertThat(member.getProfile().getPhoneNumber()).isEqualTo(
            request.getProfile().getPhoneNumber());
        assertThat(member.getProfile().getAddress()).isEqualTo(request.getProfile().getAddress());
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void deleteMember_Success_Test() {

        MemberResponse member = memberProfileFacade.createMember(request);

        String deletedLoginId = memberProfileFacade.deleteMember(member.getMemberNo());

        assertThat(deletedLoginId).isEqualTo(member.getLoginId());
    }

    @Test
    @DisplayName("회원 상세 조회 테스트")
    void findMemberDetails_Success_Test() {

        MemberResponse member = memberProfileFacade.createMember(request);

        MemberDetailsResponse findMember =
            memberProfileFacade.findMemberDetails(member.getMemberNo());

        assertThat(findMember.getMemberNo()).isEqualTo(member.getMemberNo());
        assertThat(findMember.getLoginId()).isEqualTo(member.getLoginId());
        assertThat(findMember.getName()).isEqualTo(member.getName());
        assertThat(findMember.getCreatedAt()).isEqualTo(member.getCreatedAt());
        assertThat(findMember.getProfiles()).hasSize(1);
    }

    @Test
    @DisplayName("회원 전체 조회 테스트")
    void findMembers_Success_Test() {

        memberProfileFacade.createMember(request);

        generateMembers();

        List<MemberInfoResponse> responses = memberProfileFacade.findMembers(0, 10, "");

        assertThat(responses).hasSize(10);
    }

    @Test
    @DisplayName("회원 전체 조회 이름 검색 테스트")
    void findMembers_ByName_Success_Test() {

        memberProfileFacade.createMember(request);

        generateMembers();

        String name = "테스트";
        List<MemberInfoResponse> responses = memberProfileFacade.findMembers(0, 5, name);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo(name);
        assertThat(responses.get(0).getName()).isEqualTo(request.getName());
        assertThat(responses.get(0).getLoginId()).isEqualTo(request.getLoginId());
    }

    @Test
    @DisplayName("프로필 생성 테스트")
    void createProfile_Success_Test() {

        MemberResponse member = memberProfileFacade.createMember(request);

        CreateProfileRequest profileRequest =
            generateCreateProfileRequest();

        ProfileResponse response =
            memberProfileFacade.createProfile(profileRequest, member.getMemberNo());

        assertThat(response.getProfileNo()).isPositive();
        assertThat(response.getNickname()).isEqualTo(profileRequest.getNickname());
        assertThat(response.getPhoneNumber()).isEqualTo(profileRequest.getPhoneNumber());
        assertThat(response.getAddress()).isEqualTo(profileRequest.getAddress());
    }

    @Test
    @DisplayName("프로필 수정 테스트")
    void updateProfile_Success_Test() {

        MemberResponse member = memberProfileFacade.createMember(request);

        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest(
            "닉네임생성",
            "01099999999",
            "서울특별시 성북구 화랑도 11길 26 103동 1602호 (하월곡동, 한국아파트)",
            ProfileStatus.MAIN
        );

        ProfileResponse response =
            memberProfileFacade.updateProfile(updateProfileRequest,
                member.getProfile().getProfileNo(), member.getMemberNo());

        assertThat(response.getNickname()).isEqualTo(updateProfileRequest.getNickname());
        assertThat(response.getPhoneNumber()).isEqualTo(updateProfileRequest.getPhoneNumber());
        assertThat(response.getAddress()).isEqualTo(updateProfileRequest.getAddress());
        assertThat(response.getProfileStatus()).isEqualTo(updateProfileRequest.getProfileStatus());
        assertThat(response.getNickname()).isNotEqualTo(member.getProfile().getNickname());
        assertThat(response.getPhoneNumber()).isNotEqualTo(member.getProfile().getPhoneNumber());
        assertThat(response.getAddress()).isNotEqualTo(member.getProfile().getAddress());
    }

    @Test
    @DisplayName("프로필 삭제 테스트")
    void deleteProfile_Success_Test() {

        MemberResponse member = memberProfileFacade.createMember(request);

        CreateProfileRequest profileRequest =
            generateCreateProfileRequest();

        memberProfileFacade.createProfile(profileRequest, member.getMemberNo());

        String nickname = memberProfileFacade.deleteProfile(member.getProfile().getProfileNo(),
            member.getMemberNo());

        assertThat(nickname).isEqualTo(member.getProfile().getNickname());
    }

    private void generateMembers() {
        for (int i = 0; i < 20; i++) {
            CreateProfileRequest profileDto = new CreateProfileRequest("홍길동", "01098765432",
                "서울특별시 종로구 청계천로 85 17층(관철동, 삼일빌딩) 한국지역정보개발원");

            CreateMemberRequest memberRequest = new CreateMemberRequest(
                "member" + i,
                "회원",
                "test123@" + i,
                profileDto
            );

            memberProfileFacade.createMember(memberRequest);
        }
    }

    private static CreateProfileRequest generateCreateProfileRequest() {
        CreateProfileRequest profileRequest = new CreateProfileRequest(
            "닉네임생성",
            "01099999999",
            "서울특별시 성북구 화랑도 11길 26 103동 1602호 (하월곡동, 한국아파트)"
        );
        return profileRequest;
    }
}