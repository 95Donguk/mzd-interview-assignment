package kr.co.mz.mzdinterviewassignment.dto.response.member;

import java.time.LocalDateTime;
import kr.co.mz.mzdinterviewassignment.domain.member.MemberStatus;
import kr.co.mz.mzdinterviewassignment.dto.response.profile.ProfileResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class MemberDetailResponseWithProfileModel {

  private Long memberNo;
  private String loginId;
  private String name;
  private String password;
  private MemberStatus memberStatus;
  private LocalDateTime updatedAt;
  private LocalDateTime createdAt;
  private CollectionModel<EntityModel<ProfileResponse>> profiles;
}
