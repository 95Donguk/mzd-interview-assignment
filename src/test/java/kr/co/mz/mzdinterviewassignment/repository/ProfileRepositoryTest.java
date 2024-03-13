package kr.co.mz.mzdinterviewassignment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import kr.co.mz.mzdinterviewassignment.domain.profile.Profile;
import kr.co.mz.mzdinterviewassignment.domain.profile.ProfileStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
            .loginId("testid")
            .name("테스트")
            .password("testPassword")
            .build();

        memberRepository.saveAndFlush(member);
    }

    @Test
    @DisplayName("메인 프로필이 존재하지 않은 회원의 프로필 조회 테스트")
    void findProfileByMemberAndProfileStatus_HasNotMainProfile_Test() {

        Profile profile =
            generateProfile(ProfileStatus.NORMAL);

        profileRepository.saveAndFlush(profile);

        Optional<Profile> findMainProfile =
            profileRepository.findProfileByMemberAndProfileStatus(member, ProfileStatus.MAIN);

        assertThat(findMainProfile).isEmpty();
    }

    @Test
    @DisplayName("일반 프로필이 존재하지 않은 회원의 프로필 조회 테스트")
    void findProfileByMemberAndProfileStatus_HasNotNormalProfile_Test() {

        Profile profile =
            generateProfile(ProfileStatus.MAIN);

        profileRepository.saveAndFlush(profile);

        Optional<Profile> findMainProfile =
            profileRepository.findProfileByMemberAndProfileStatus(member, ProfileStatus.NORMAL);

        assertThat(findMainProfile).isEmpty();
    }

    @Test
    @DisplayName("회원의 일반 프로필 조회 테스트")
    void findProfileByMemberAndProfileStatus_HasNormalProfile_Test() {

        Profile profile =
            generateProfile(ProfileStatus.NORMAL);

        Profile savedProfile = profileRepository.saveAndFlush(profile);

        Profile findNormalProfile =
            profileRepository.findProfileByMemberAndProfileStatus(member, ProfileStatus.NORMAL)
                .orElseThrow();

        assertThat(findNormalProfile.getProfileNo()).isEqualTo(savedProfile.getProfileNo());
        assertThat(findNormalProfile.getNickname()).isEqualTo(savedProfile.getNickname());
        assertThat(findNormalProfile.getPhoneNumber()).isEqualTo(savedProfile.getPhoneNumber());
        assertThat(findNormalProfile.getAddress()).isEqualTo(savedProfile.getAddress());
        assertThat(findNormalProfile.getProfileStatus()).isEqualTo(savedProfile.getProfileStatus());
        assertThat(findNormalProfile.getMember()).isEqualTo(savedProfile.getMember());
    }


    @Test
    @DisplayName("회원의 메인 프로필 조회 테스트")
    void findProfileByMemberAndProfileStatus_HasMainProfile_Test() {

        Profile profile =
            generateProfile(ProfileStatus.MAIN);

        Profile savedProfile = profileRepository.saveAndFlush(profile);

        Profile findNormalProfile =
            profileRepository.findProfileByMemberAndProfileStatus(member, ProfileStatus.MAIN)
                .orElseThrow();

        assertThat(findNormalProfile.getProfileNo()).isEqualTo(savedProfile.getProfileNo());
        assertThat(findNormalProfile.getNickname()).isEqualTo(savedProfile.getNickname());
        assertThat(findNormalProfile.getPhoneNumber()).isEqualTo(savedProfile.getPhoneNumber());
        assertThat(findNormalProfile.getAddress()).isEqualTo(savedProfile.getAddress());
        assertThat(findNormalProfile.getProfileStatus()).isEqualTo(savedProfile.getProfileStatus());
        assertThat(findNormalProfile.getMember()).isEqualTo(savedProfile.getMember());
    }

    @Test
    @DisplayName("프로필이 없는 회원의 프로필 조회 테스트")
    void findAllByMember_HasNotProfile_Test() {
        Member otherMember = Member.builder()
            .loginId("otherid")
            .name("다른이름")
            .password("otherPassword")
            .build();

        memberRepository.saveAndFlush(otherMember);

        List<Profile> profiles = generateProfiles();

        profileRepository.saveAllAndFlush(profiles);

        List<Profile> findProfiles = profileRepository.findAllByMember(otherMember);

        assertThat(findProfiles).isEmpty();
    }

    @Test
    @DisplayName("회원의 프로필 조회 테스트")
    void findAllByMember_HasProfile_Test() {
        List<Profile> profiles = generateProfiles();

        profileRepository.saveAllAndFlush(profiles);

        List<Profile> findProfiles = profileRepository.findAllByMember(member);

        findProfiles.forEach(profile -> assertThat(profile.getMember()).isEqualTo(member));
        assertThat(findProfiles).hasSize(30);
    }

    private Profile generateProfile(final ProfileStatus status) {
        return Profile.builder()
            .member(member)
            .nickname("테스트별명")
            .phoneNumber("01012345678")
            .address("서울특별시 성북구 화랑도 11길 26 103동 1602호 (하월곡동, 한국아파트)")
            .profileStatus(status)
            .build();
    }

    private List<Profile> generateProfiles() {
        return IntStream.range(0, 30)
            .mapToObj(i -> Profile.builder()
                .member(member)
                .nickname("테스트별명" + i)
                .phoneNumber("010123456" + i)
                .address("서울특별시 성북구 화랑도 11길 " + i + " 103동 1602호 (하월곡동, 한국아파트)")
                .profileStatus(i == 0 ? ProfileStatus.MAIN : ProfileStatus.NORMAL)
                .build())
            .toList();
    }
}