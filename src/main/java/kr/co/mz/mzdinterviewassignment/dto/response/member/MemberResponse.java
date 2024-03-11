package kr.co.mz.mzdinterviewassignment.dto.response.member;

import java.time.LocalDateTime;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
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

    public static MemberResponse generateMemberResponse(final Member member) {
        return MemberResponse.builder()
            .memberNo(member.getMemberNo())
            .loginId(member.getLoginId())
            .name(member.getName())
            .createdAt(member.getCreatedAt())
            .build();
    }
}
