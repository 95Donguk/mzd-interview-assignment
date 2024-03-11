package kr.co.mz.mzdinterviewassignment.dto.response.member;

import java.time.LocalDateTime;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import kr.co.mz.mzdinterviewassignment.domain.member.MemberStatus;
import kr.co.mz.mzdinterviewassignment.dto.response.profile.ProfileResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class MemberInfoResponse {
    private Long memberNo;
    private String loginId;
    private String name;
    private String password;
    private MemberStatus memberStatus;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private ProfileResponse mainProfile;

    public static MemberInfoResponse generateMemberInfo(final Member member,
                                                        final ProfileResponse mainProfile) {
        return MemberInfoResponse.builder()
            .memberNo(member.getMemberNo())
            .loginId(member.getLoginId())
            .name(member.getName())
            .password(member.getPassword())
            .memberStatus(member.getMemberStatus())
            .updatedAt(member.getUpdatedAt())
            .createdAt(member.getCreatedAt())
            .mainProfile(mainProfile)
            .build();
    }
}
