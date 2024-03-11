package kr.co.mz.mzdinterviewassignment.service;

import java.util.List;
import java.util.Objects;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import kr.co.mz.mzdinterviewassignment.domain.profile.Profile;
import kr.co.mz.mzdinterviewassignment.domain.profile.ProfileStatus;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.CreateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.UpdateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.response.profile.ProfileResponse;
import kr.co.mz.mzdinterviewassignment.exception.profile.EmptyProfileException;
import kr.co.mz.mzdinterviewassignment.exception.profile.NonMatchMemberNoException;
import kr.co.mz.mzdinterviewassignment.exception.profile.NotFoundProfileException;
import kr.co.mz.mzdinterviewassignment.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {
    private ProfileRepository profileRepository;

    @Transactional
    public ProfileResponse createProfile(final CreateProfileRequest dto, final Member member) {

        log.info("프로필 생성 시작");

        List<Profile> profiles = profileRepository.findAllByMember(member);

        ProfileStatus profileStatus = setProfileStatus(profiles);
        log.info("프로필 상태 {}", profileStatus.name());

        Profile savedProfile = profileRepository.save(dto.toEntity(profileStatus, member));
        log.info("프로필 생성 완료 {}", savedProfile);
        return ProfileResponse.generateProfile(savedProfile);
    }

    @Transactional
    public ProfileResponse updateProfile(final UpdateProfileRequest dto,
                                         final Long profileNo,
                                         final Member member) {

        log.info("프로필 수정 시작");
        Profile profile = profileRepository.findById(profileNo)
            .orElseThrow(() -> new NotFoundProfileException(profileNo));

        checkMatchMemberNo(member, profile);

        if (isMainProfile(dto.getProfileStatus())) {
            log.info("기존 메인 프로필을 일반 프로필로 전환");
            profileRepository.findAllByMember(member)
                .stream().filter(p -> p.getProfileStatus().equals(ProfileStatus.MAIN))
                .forEach(p -> p.updateProfileStatus(ProfileStatus.NORMAL));
        }

        Profile updatedProfile = profile.update(dto.getNickname(), dto.getPhoneNumber(), dto.getAddress(),
            dto.getProfileStatus());

        log.info("프로필 수정 완료");
        return ProfileResponse.generateProfile(updatedProfile);
    }

    @Transactional
    public String deleteProfile(final Long profileNo, final Member member) {
        log.info("프로필 삭제 시작");

        Profile profile = profileRepository.findById(profileNo)
            .orElseThrow(() -> new NotFoundProfileException(profileNo));

        checkMatchMemberNo(member, profile);

        profileRepository.delete(profile);
        log.info("프로필 삭제 완료");

        return profile.getNickname();
    }

    public ProfileResponse findMainProfile(final Member member) {
        log.info("{} 의 메인 프로필 조회 시작", member.getLoginId());

        Profile profile =
            profileRepository.findProfileByMemberAndProfileStatus(member, ProfileStatus.MAIN)
                .orElseGet(() ->profileRepository.findAllByMember(member)
                    .stream().findFirst()
                    .orElseThrow(() -> new EmptyProfileException(member.getMemberNo())
                ));

        log.info("{} 의 메인 프로필 조회 완료", member.getLoginId());

        return ProfileResponse.generateProfile(profile);
    }

    public List<ProfileResponse> findProfiles(final Member member) {
        log.info("{} 의 프로필 조회");

        return profileRepository.findAllByMember(member)
            .stream()
            .map(ProfileResponse::generateProfile)
            .toList();
    }

    private boolean isMainProfile(final ProfileStatus profileStatus) {
        log.info("수정할 프로필을 메인 프로필로 상태 변경할 것인지 확인");
        return profileStatus.equals(ProfileStatus.MAIN);
    }

    private void checkMatchMemberNo(final Member member, final Profile profile) {
        log.info("프로필과 매핑된 회원 식별 번호 확인");

        Long memberNo = member.getMemberNo();
        Long profileMemberNo = profile.getMember().getMemberNo();

        if (isNonMatchMemberNo(memberNo, profileMemberNo)) {
            throw new NonMatchMemberNoException(memberNo, profileMemberNo);
        }
    }

    private boolean isNonMatchMemberNo(final Long memberNo, final Long profileMemberNo) {
        return !Objects.equals(profileMemberNo, memberNo);
    }

    private ProfileStatus setProfileStatus(final List<Profile> profiles) {

        log.info("프로필 상태 지정");
        if (hasMainProfile(profiles)) {
            return ProfileStatus.NORMAL;
        }

        return ProfileStatus.MAIN;
    }

    private boolean hasMainProfile(final List<Profile> profiles) {
        return profiles.stream().anyMatch(profile -> profile.getProfileStatus().equals(
            ProfileStatus.MAIN));
    }
}
