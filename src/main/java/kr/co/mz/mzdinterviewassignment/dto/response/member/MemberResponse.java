package kr.co.mz.mzdinterviewassignment.dto.response.member;

import java.time.LocalDateTime;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
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
public class MemberResponse {
    private Long memberNo;
    private String loginId;
    private String name;
    private LocalDateTime createdAt;
    private ProfileResponse profile;

    public static MemberResponse generateMemberResponse(final Member member,
                                                        final ProfileResponse profile) {
        return MemberResponse.builder()
            .memberNo(member.getMemberNo())
            .loginId(member.getLoginId())
            .name(member.getName())
            .createdAt(member.getCreatedAt())
            .profile(profile)
            .build();
    }
}
