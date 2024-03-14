package kr.co.mz.mzdinterviewassignment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import kr.co.mz.mzdinterviewassignment.domain.profile.Profile;
import kr.co.mz.mzdinterviewassignment.domain.profile.ProfileStatus;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.CreateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.UpdateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.response.profile.ProfileResponse;
import kr.co.mz.mzdinterviewassignment.exception.profile.CannotDeleteProfileException;
import kr.co.mz.mzdinterviewassignment.exception.profile.EmptyProfileException;
import kr.co.mz.mzdinterviewassignment.exception.profile.NotFoundProfileException;
import kr.co.mz.mzdinterviewassignment.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @ExtendWith(MockitoExtension.class) 어노테이션 추가
 * - 테스트 코드에 대한 정보를 추출하기 위해 사용
 * - 스프링 부트에서는 테스트 코드에 대한 정보를 추출하기 위해 사용
 * - @ExtendWith: 단위 테스트에 공통적으로 확장 기능을 선언해주는 역할
 * - MockitoExtend.class : JUnit과 모키토를 연동해 테스트를 진행할려고 함
 */
@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    /**
     * @Mock
     * - 목 객체를 생성한다
     * - 메서드를 갖고 있지만 내부 구현이 없는 상태로 지정
     * - @Spy는 모든 기능을 가지고 있는 완전환 객체로 Stub하지 않은 메서드들은 원본 메소드 그대로 사용한다.
     *      - 일부분 만 모킹하는 것
     *      - 외부 라이브러리를 이용한 테스트에 자주 사용
     * - Stub
     *      - 다른 객체 대신에 가짜 객체(Mock)를 주입하여 어떤 결과를 반환하라고 정해진 답변을 준비 시킴
     */
    @Mock
    private ProfileRepository profileRepository;

    /**
     * @InjectMocks
     * - @Mock 또는 @Spy로 생성된 가짜 객체를 자동으로 주입시켜주는 객체
     * - @InjectMocks 객체에서 사용할 객체를 @Mock으로 만들어 쓰면 된다.
     */
    @InjectMocks
    private ProfileService profileService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
            .loginId("testid")
            .name("테스트")
            .password("testPassword")
            .build();
    }

    @Test
    @DisplayName("회원에 메인 프로필이 없을 때 생성하는 프로필을 메인 프로필로 자동 지정 후 프로필 생성")
    void createProfile_setMainProfile_Test() {

        List<Profile> profiles = IntStream.range(0, 5)
            .mapToObj(i -> Profile.builder()
                .member(member)
                .nickname("테스트" + i)
                .phoneNumber("0101234567" + i)
                .address("서울특별시 성북구 화랑도 11길 " + i + " 103동 1602호 (하월곡동, 한국아파트)")
                .profileStatus(ProfileStatus.NORMAL)
                .build())
            .toList();

        CreateProfileRequest dto = new CreateProfileRequest("홍길동", "01098765432",
            "서울특별시 종로구 청계천로 85 17층(관철동, 삼일빌딩) 한국지역정보개발원");

        /**
         * thenReturn()
         * - 실제 메서드를 호출하지만, 리턴 값을 임의로 정의할 수 있다.
         * - 메서드 작업이 오래 걸리는 경우 끝날 때까지 기다려야 한다.
         * - 실제 메서드를 호출하기 때문에 대상 메소드에 문제점이 있을 경우 발견할 수 있다.
         */
        Mockito.when(profileRepository.findAllByMember(any(Member.class)))
            .thenReturn(profiles);

        /**
         * when()
         *  - 어떤 동작을 할 때 ~
         * then()
         *  - 반환 됨
         *  returnsFirstArg()
         *  - 메서드의 첫번째 인자 반환
         */
        Mockito.when(profileRepository.save(any(Profile.class)))
            .then(returnsFirstArg());

        ProfileResponse response = profileService.createProfile(dto, member);

        assertThat(response.getProfileStatus()).isEqualTo(ProfileStatus.MAIN);
        assertThat(response.getNickname()).isEqualTo(dto.getNickname());
        assertThat(response.getPhoneNumber()).isEqualTo(dto.getPhoneNumber());
        assertThat(response.getAddress()).isEqualTo(dto.getAddress());

        Mockito.verify(profileRepository, Mockito.times(1)).findAllByMember(any(Member.class));
        Mockito.verify(profileRepository, Mockito.times(1)).save(any(Profile.class));
    }

    @Test
    @DisplayName("회원에 메인 프로필이 있을 때 생성하는 프로필을 일반 프로필로 자동 지정 후 프로필 생성")
    void createProfile_setNormalProfile_Test() {

        List<Profile> profiles = generateProfiles();

        CreateProfileRequest dto = new CreateProfileRequest("홍길동", "01098765432",
            "서울특별시 종로구 청계천로 85 17층(관철동, 삼일빌딩) 한국지역정보개발원");

        Mockito.when(profileRepository.findAllByMember(any(Member.class)))
            .thenReturn(profiles);

        Mockito.when(profileRepository.save(any(Profile.class)))
            .then(returnsFirstArg());

        ProfileResponse response = profileService.createProfile(dto, member);

        assertThat(response.getProfileStatus()).isEqualTo(ProfileStatus.NORMAL);
        assertThat(response.getNickname()).isEqualTo(dto.getNickname());
        assertThat(response.getPhoneNumber()).isEqualTo(dto.getPhoneNumber());
        assertThat(response.getAddress()).isEqualTo(dto.getAddress());

        Mockito.verify(profileRepository, Mockito.times(1)).findAllByMember(any(Member.class));
        Mockito.verify(profileRepository, Mockito.times(1)).save(any(Profile.class));
    }

    @Test
    @DisplayName("프로필 식별 번호로 프로필을 찾을 수 없을 시 프로필 수정 실패 테스트 ")
    void updateProfile_notFoundProfile_Fail_Test() {

        UpdateProfileRequest dto = new UpdateProfileRequest("홍길동", "01098765432",
            "서울특별시 종로구 청계천로 85 17층(관철동, 삼일빌딩) 한국지역정보개발원", ProfileStatus.MAIN);

        Mockito.when(profileRepository.findById(any(Long.class)))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.updateProfile(dto, 1L, member))
            .isInstanceOf(NotFoundProfileException.class);

        Mockito.verify(profileRepository, Mockito.times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("프로필 수정 성공 테스트 ")
    void updateProfile_Success_Test() {
        Profile profile = generateProfile();

        UpdateProfileRequest dto = new UpdateProfileRequest("홍길동", "01098765432",
            "서울특별시 종로구 청계천로 85 17층(관철동, 삼일빌딩) 한국지역정보개발원", ProfileStatus.MAIN);

        Mockito.when(profileRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(profile));

        profileService.updateProfile(dto, 1L, member);

        assertThat(profile.getNickname()).isEqualTo(dto.getNickname());
        assertThat(profile.getPhoneNumber()).isEqualTo(dto.getPhoneNumber());
        assertThat(profile.getAddress()).isEqualTo(dto.getAddress());

        Mockito.verify(profileRepository, Mockito.times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("일반 프로필을 메인 프로필로 상태 수정 성공 테스트")
    void updateProfile_ChangeMainProfile_Success_Test() {
        List<Profile> profiles = generateProfiles();

        UpdateProfileRequest dto = new UpdateProfileRequest("신사임당", "0114321234",
            null, ProfileStatus.MAIN);

        Mockito.when(profileRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(profiles.get(4)));

        Mockito.when(profileRepository.findAllByMember(any(Member.class)))
            .thenReturn(profiles);

        profileService.updateProfile(dto, 4L, member);

        assertThat(profiles.get(4).getNickname()).isEqualTo(dto.getNickname());
        assertThat(profiles.get(4).getPhoneNumber()).isEqualTo(dto.getPhoneNumber());
        assertThat(profiles.get(4).getAddress()).isEqualTo(dto.getAddress());
        assertThat(profiles.get(4).getProfileStatus()).isEqualTo(dto.getProfileStatus());

        for (int i = 0; i < profiles.size() - 1; i++) {
            assertThat(profiles.get(i).getProfileStatus()).isEqualTo(ProfileStatus.NORMAL);
        }

        Mockito.verify(profileRepository, Mockito.times(1)).findById(any(Long.class));
        Mockito.verify(profileRepository, Mockito.times(1)).findAllByMember(any(Member.class));
    }

    @Test
    @DisplayName("프로필이 하나 밖에 없을 때 일반 프로필로 상태만 변경 불가 수정 테스트")
    void updateProfile_HasSingleProfileDoNotChangeProfileStatus_Test() {

        Profile profile = generateProfile();

        UpdateProfileRequest dto = new UpdateProfileRequest("신사임당", "0114321234",
            null, ProfileStatus.NORMAL);

        Mockito.when(profileRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(profile));

        Mockito.when(profileRepository.findAllByMember(any(Member.class)))
            .thenReturn(Collections.singletonList(profile));

        profileService.updateProfile(dto, 1L, member);

        assertThat(profile.getNickname()).isEqualTo(dto.getNickname());
        assertThat(profile.getPhoneNumber()).isEqualTo(dto.getPhoneNumber());
        assertThat(profile.getAddress()).isEqualTo(dto.getAddress());
        assertThat(profile.getProfileStatus()).isNotEqualTo(dto.getProfileStatus());

        Mockito.verify(profileRepository, Mockito.times(1)).findById(any(Long.class));
        Mockito.verify(profileRepository, Mockito.times(1)).findAllByMember(any(Member.class));
    }

    @Test
    @DisplayName("메인 프로필을 일반 프로필로 전환한다면 회원의 다른 프로필 하나를 메인프로필로 전환 테스트")
    void updateProfile_IfChangeMainProfileToNormalProfile_Test() {
        List<Profile> profiles = generateProfiles();

        UpdateProfileRequest dto = new UpdateProfileRequest("신사임당", "0114321234",
            null, ProfileStatus.NORMAL);

        Mockito.when(profileRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(profiles.get(0)));

        Mockito.when(profileRepository.findAllByMember(any(Member.class)))
            .thenReturn(profiles);

        profileService.updateProfile(dto, 1L, member);

        assertThat(profiles.get(0).getProfileStatus()).isEqualTo(dto.getProfileStatus());
        assertThat(profiles.get(0).getNickname()).isEqualTo(dto.getNickname());
        assertThat(profiles.get(0).getPhoneNumber()).isEqualTo(dto.getPhoneNumber());
        assertThat(profiles.get(0).getAddress()).isEqualTo(dto.getAddress());

        assertThat(profiles.stream()
            .anyMatch(profile -> Objects.equals(profile.getProfileStatus(), ProfileStatus.MAIN)))
            .isTrue();

        Mockito.verify(profileRepository, Mockito.times(1)).findById(any(Long.class));
        Mockito.verify(profileRepository, Mockito.times(1)).findAllByMember(any(Member.class));
    }

    @Test
    @DisplayName("프로필 식별 번호로 프로필을 찾을 수 없을 시 프로필 삭제 실패 테스트 ")
    void deleteProfile_NotFoundProfile_Fail_Test() {

        Mockito.when(profileRepository.findById(any(Long.class)))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.deleteProfile(1L, member))
            .isInstanceOf(NotFoundProfileException.class);

        Mockito.verify(profileRepository, Mockito.times(1)).findById(any(Long.class));
        Mockito.verify(profileRepository, Mockito.never()).delete(any(Profile.class));
    }

    @Test
    @DisplayName("회원의 프로필이 1개만 있을 때 프로필 삭제 시 실패 테스트")
    void deleteProfile_SingleProfile_Fail_Test() {
        Profile profile = generateProfile();

        Mockito.when(profileRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(profile));

        Mockito.when(profileRepository.findAllByMember(any(Member.class)))
            .thenReturn(Collections.singletonList(profile));

        assertThatThrownBy(() -> profileService.deleteProfile(1L, member))
            .isInstanceOf(CannotDeleteProfileException.class);

        Mockito.verify(profileRepository, Mockito.times(1)).findById(any(Long.class));
        Mockito.verify(profileRepository, Mockito.never()).delete(any(Profile.class));
    }

    @Test
    @DisplayName("프로필 삭제 성공 테스트")
    void deleteProfile_Success_Test() {
        Profile profile = generateProfile();

        Mockito.when(profileRepository.findById(any(Long.class)))
            .thenReturn(Optional.of(profile));

        profileService.deleteProfile(1L, member);

        Mockito.verify(profileRepository, Mockito.times(1)).findById(any(Long.class));
        Mockito.verify(profileRepository, Mockito.times(1)).delete(any(Profile.class));
    }

    @Test
    @DisplayName("회원이 프로필을 가지고 있지 않을 시 메인 프로필 조회 실패 테스트")
    void findMainProfile_EmptyProfile_Fail_Test() {
        Mockito.when(profileRepository.findProfileByMemberAndProfileStatus(any(Member.class), any(
            ProfileStatus.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> profileService.findMainProfile(member))
            .isInstanceOf(EmptyProfileException.class);

        Mockito.verify(profileRepository, Mockito.times(1))
            .findProfileByMemberAndProfileStatus(any(Member.class), any(ProfileStatus.class));
        Mockito.verify(profileRepository, Mockito.times(1)).findAllByMember(any(Member.class));
    }

    @Test
    @DisplayName("메인 프로필 조회 테스트")
    void findMainProfile_Test() {
        Profile profile = generateProfile();

        Mockito.when(profileRepository.findProfileByMemberAndProfileStatus(any(Member.class), any(
            ProfileStatus.class))).thenReturn(Optional.of(profile));

        ProfileResponse response = profileService.findMainProfile(member);

        assertThat(response.getProfileStatus()).isEqualTo(ProfileStatus.MAIN);
        assertThat(response.getNickname()).isEqualTo(profile.getNickname());
        assertThat(response.getPhoneNumber()).isEqualTo(profile.getPhoneNumber());
        assertThat(response.getAddress()).isEqualTo(profile.getAddress());

        Mockito.verify(profileRepository, Mockito.times(1))
            .findProfileByMemberAndProfileStatus(any(Member.class), any(ProfileStatus.class));
        Mockito.verify(profileRepository, Mockito.never()).findAllByMember(any(Member.class));
    }

    @Test
    @DisplayName("회원의 메인 프로필 조회 시 메인 프로필이 없다면 일반 프로필로 대체 조회 테스트")
    void findMainProfile_NotFoundMainProfileAndFindProfile_Test() {
        List<Profile> profiles = IntStream.range(0, 5)
            .mapToObj(i -> Profile.builder()
                .member(member)
                .nickname("테스트별명" + i)
                .phoneNumber("0101234567" + i)
                .address("서울특별시 성북구 화랑도 11길 " + i + " 103동 1602호 (하월곡동, 한국아파트)")
                .profileStatus(ProfileStatus.NORMAL)
                .build())
            .toList();

        Profile profile = profiles.stream().findFirst().orElseThrow();

        Mockito.when(profileRepository.findProfileByMemberAndProfileStatus(any(Member.class), any(
            ProfileStatus.class))).thenReturn(Optional.empty());

        Mockito.when(profileRepository.findAllByMember(any(Member.class)))
            .thenReturn(profiles);

        ProfileResponse response = profileService.findMainProfile(member);

        assertThat(response.getProfileStatus()).isEqualTo(ProfileStatus.NORMAL);
        assertThat(response.getNickname()).isEqualTo(profile.getNickname());
        assertThat(response.getPhoneNumber()).isEqualTo(profile.getPhoneNumber());
        assertThat(response.getAddress()).isEqualTo(profile.getAddress());
        assertThat(response.getProfileStatus()).isEqualTo(profile.getProfileStatus());


        Mockito.verify(profileRepository, Mockito.times(1))
            .findProfileByMemberAndProfileStatus(any(Member.class), any(ProfileStatus.class));
        Mockito.verify(profileRepository, Mockito.times(1)).findAllByMember(any(Member.class));
    }

    @Test
    @DisplayName("프로필이 없는 회원의 전체 프로필 조회 시 실패 테스트")
    void findProfiles_EmptyProfile_Fail_Test() {

        Mockito.when(profileRepository.findAllByMember(any(Member.class)))
            .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> profileService.findProfiles(member))
            .isInstanceOf(EmptyProfileException.class);

        Mockito.verify(profileRepository, Mockito.times(1)).findAllByMember(any(Member.class));
    }

    @Test
    @DisplayName("회원의 전체 프로필 조회 테스트")
    void findProfiles_Test() {
        List<Profile> profiles = generateProfiles();

        Mockito.when(profileRepository.findAllByMember(any(Member.class)))
            .thenReturn(profiles);

        List<ProfileResponse> responses = profileService.findProfiles(member);

        for (int i = 0; i < responses.size(); i++) {
            ProfileResponse response = responses.get(i);
            Profile profile = profiles.get(i);
            assertThat(response.getNickname()).isEqualTo(profile.getNickname());
            assertThat(response.getPhoneNumber()).isEqualTo(profile.getPhoneNumber());
            assertThat(response.getAddress()).isEqualTo(profile.getAddress());
            assertThat(response.getProfileStatus()).isEqualTo(profile.getProfileStatus());
        }

        Mockito.verify(profileRepository, Mockito.times(1)).findAllByMember(any(Member.class));
    }

    private Profile generateProfile() {
        return Profile.builder()
            .member(member)
            .nickname("테스트별명")
            .phoneNumber("01012345678")
            .address("서울특별시 성북구 화랑도 11길 26 103동 1602호 (하월곡동, 한국아파트)")
            .profileStatus(ProfileStatus.MAIN)
            .build();
    }

    private List<Profile> generateProfiles() {
        return IntStream.range(0, 5)
            .mapToObj(i -> Profile.builder()
                .member(member)
                .nickname("테스트별명" + i)
                .phoneNumber("0101234567" + i)
                .address("서울특별시 성북구 화랑도 11길 " + i + " 103동 1602호 (하월곡동, 한국아파트)")
                .profileStatus(i == 0 ? ProfileStatus.MAIN : ProfileStatus.NORMAL)
                .build())
            .toList();
    }
}