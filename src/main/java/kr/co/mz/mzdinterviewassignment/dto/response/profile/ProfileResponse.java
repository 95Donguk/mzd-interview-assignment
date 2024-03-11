package kr.co.mz.mzdinterviewassignment.dto.response.profile;

import java.time.LocalDateTime;
import kr.co.mz.mzdinterviewassignment.domain.profile.Profile;
import kr.co.mz.mzdinterviewassignment.domain.profile.ProfileStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ProfileResponse {
    private Long profileNo;
    private String nickname;
    private String phoneNumber;
    private String address;
    private ProfileStatus profileStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProfileResponse generateProfile(final Profile profile) {
        return ProfileResponse.builder()
            .profileNo(profile.getProfileNo())
            .nickname(profile.getNickname())
            .phoneNumber(profile.getPhoneNumber())
            .address(profile.getAddress())
            .profileStatus(profile.getProfileStatus())
            .createdAt(profile.getCreatedAt())
            .updatedAt(profile.getUpdatedAt())
            .build();
    }
}
