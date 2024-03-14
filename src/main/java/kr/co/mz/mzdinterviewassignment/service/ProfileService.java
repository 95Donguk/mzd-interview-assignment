package kr.co.mz.mzdinterviewassignment.service;

import java.util.List;
import java.util.Objects;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import kr.co.mz.mzdinterviewassignment.domain.profile.Profile;
import kr.co.mz.mzdinterviewassignment.domain.profile.ProfileStatus;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.CreateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.UpdateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.response.profile.ProfileResponse;
import kr.co.mz.mzdinterviewassignment.exception.profile.CannotDeleteProfileException;
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
    public static final int MIN_PROFILES_COUNT = 1;

    private final ProfileRepository profileRepository;

    @Transactional
    public ProfileResponse createProfile(final CreateProfileRequest dto, final Member member) {

        log.info("프로필 생성 시작");

        List<Profile> profiles = profileRepository.findAllByMember(member);

        ProfileStatus profileStatus = setProfileStatus(profiles);
        log.info("프로필 상태 {}", profileStatus.name());

        Profile savedProfile = profileRepository.save(dto.toEntity(profileStatus, member));
        log.info("프로필 생성 완료 회원 식별 번호 : {}", savedProfile.getMember().getLoginId());
        return ProfileResponse.generateProfile(savedProfile);
    }

    @Transactional
    public ProfileResponse updateProfile(final UpdateProfileRequest dto,
                                         final Long profileNo,
                                         final Member member) {

        log.info("프로필 수정 시작");
        Profile profile = profileRepository.findById(profileNo)
            .orElseThrow(() -> new NotFoundProfileException(profileNo));

        /**
         * 회원 식별 번호가 일치하는지 확인
         */
        checkMatchMemberNo(member, profile);

        List<Profile> profiles = profileRepository.findAllByMember(member);

        /**
         * 프로필 수정 시 회원이 가지고 있는 프로필이 1개 라면 회원 프로필 상태는 무조건 MAIN으로 설정
         */
        if (profiles.size() == MIN_PROFILES_COUNT) {
            Profile updatedProfile =
                profile.update(dto.getNickname(), dto.getPhoneNumber(), dto.getAddress(),
                    ProfileStatus.MAIN);
            /**
             * saveAndFlush의 의미는 엔티티를 저장하고 영속성 컨텍스트의 변경을 즉시 데이터베이스에 플러시 하는 메서드로
             * 변경 내용을 즉시 데이터베이스에 반영합니다.
             * 여기서 선언한 이유는 프로필이 업데이트하고 응답 데이터에 수정 일자 변경을 즉시 반영하기 위해 설정했습니다.
             * 추측인데 여기서 saveAndFlush를 하지않으면 BaseEntity의 updatedAt 필드가 데이터 변경 감지를 못하고
             * 물리 트랜잭션이 끝나고 감지가 되서 updatedAt 필드는 전의 데이터로 남겨진다고 생각해서 사용했습니다.
             */
            profileRepository.saveAndFlush(updatedProfile);
            return ProfileResponse.generateProfile(updatedProfile);
        }

        /**
         * 밑의 로직은 프로필이 2개 이상일 때 사용되는 로직으로 수정할 프로필이 메인프로필로 바꾼다면
         */
        if (isMainProfile(dto.getProfileStatus())) {
            log.info("기존 메인 프로필을 일반 프로필로 전환");
            /**
             * 임의로 데이터베이스에 직접 메인 프로필을 여러 개 지정해 놓았다면 foreach로 모두 일반프로필로 전환
             */
            profiles.forEach(p -> p.updateProfileStatus(ProfileStatus.NORMAL));
        } else if (isMainProfile(profile.getProfileStatus())) {
            log.info("메인 프로필을 일반 프로필로 전환으로 인해 회원의 다른 프로필을 메인 프로필로 임의 지정");
            profiles
                .stream().filter(p -> p.getProfileStatus().equals(ProfileStatus.NORMAL))
                .findFirst()
                .ifPresent(p -> p.updateProfileStatus(ProfileStatus.MAIN));
        }

        Profile updatedProfile = profile.update(dto.getNickname(), dto.getPhoneNumber(), dto.getAddress(),
            dto.getProfileStatus());
        profileRepository.saveAndFlush(updatedProfile);
        return ProfileResponse.generateProfile(updatedProfile);
    }

    @Transactional
    public String deleteProfile(final Long profileNo, final Member member) {
        log.info("프로필 삭제 시작");

        Profile profile = profileRepository.findById(profileNo)
            .orElseThrow(() -> new NotFoundProfileException(profileNo));

        checkMatchMemberNo(member, profile);

        /**
         * 회원 프로필 전체를 찾아서
         */
        List<Profile> profiles = profileRepository.findAllByMember(member);

        /**
         * 프로필이 하나 밖에 없다면 삭제 불가
         */
        if (profiles.size() == MIN_PROFILES_COUNT) {
            throw new CannotDeleteProfileException(member.getLoginId());
        }

        profileRepository.delete(profile);
        log.info("프로필 삭제 완료");

        return profile.getNickname();
    }

    public ProfileResponse findMainProfile(final Member member) {
        log.info("{} 의 메인 프로필 조회 시작", member.getLoginId());

        /**
         * 메인 프로필이 없을 때 처리로
         * 처음 구현했을때는 에러 처리 했으나 회원 전체 조회 인데,
         * 회원이 메인 프로필이 없다고 해서 실패처리하는 것보다는 서비스를 계속 제공하는게 좋다고
         * 생각해서 메인 프로필이 없으면 일반 프로필이라도 보여주게 설정했습니다.
         * 일반 프로필도 없다면 프로필이 없어서 정보를 제공할 수 없게 예외 처리 했습니다.
         */
        Profile profile =
            profileRepository.findProfileByMemberAndProfileStatus(member, ProfileStatus.MAIN)
                .orElseGet(() -> profileRepository.findAllByMember(member)
                    .stream().findFirst()
                    .orElseThrow(() -> new EmptyProfileException(member.getLoginId())));

        log.info("{} 의 메인 프로필 조회 완료", member.getLoginId());

        return ProfileResponse.generateProfile(profile);
    }

    public List<ProfileResponse> findProfiles(final Member member) {
        log.info("{} 의 프로필 조회", member.getLoginId());

        /**
         * 회원의 전체 프로필을 찾고
         * 응답 데이터에 맞춰서 변환
         */
        List<ProfileResponse> responses = profileRepository.findAllByMember(member)
            .stream()
            .map(ProfileResponse::generateProfile)
            .toList();

        /**
         * 프로필이 없다면 예외 처리
         */
        if (responses.isEmpty()) {
            throw new EmptyProfileException(member.getLoginId());
        }

        return responses;
    }

    private boolean isMainProfile(final ProfileStatus profileStatus) {
        log.info("수정할 프로필을 메인 프로필로 상태 변경할 것인지 확인");
        return Objects.equals(profileStatus, ProfileStatus.MAIN);
    }

    private void checkMatchMemberNo(final Member member, final Profile profile) {
        log.info("프로필과 매핑된 회원 식별 번호 확인");

        Long memberNo = member.getMemberNo();
        Long profileMemberNo = profile.getMember().getMemberNo();

        if (isNonMatchMemberNo(memberNo, profileMemberNo)) {
            throw new NonMatchMemberNoException(memberNo, profileMemberNo);
        }
    }

    /**
     * 요청 쿼리 파라미터에서 가져온 회원 식별 번호와 프로필에 관계가 있는 회원 식별 번호가 같은지 확인
     */
    private boolean isNonMatchMemberNo(final Long memberNo, final Long profileMemberNo) {
        return !Objects.equals(profileMemberNo, memberNo);
    }

    /**
     * 프로필을 생성할 때 생성할 프로필을 메인 프로필로 설정할 건지 일반 프로필로 설정한 것인지를 설정함
     * 회원의 프로필에 메인 프로필이 있다면 일반 프로필, 없다면 메인 프로필로 설정합니다.
     * 혹여나 데이터베이스에서 직접 임의로 메인프로필을 일반프로필로 바꿨다면 프로필 생성시 메인프로필로 생성하도록 구현했습니다.
     */
    private ProfileStatus setProfileStatus(final List<Profile> profiles) {
        log.info("프로필 상태 지정");
        return profiles.stream()
            .anyMatch(profile -> profile.getProfileStatus().equals(ProfileStatus.MAIN))
            ? ProfileStatus.NORMAL : ProfileStatus.MAIN;
    }
}
