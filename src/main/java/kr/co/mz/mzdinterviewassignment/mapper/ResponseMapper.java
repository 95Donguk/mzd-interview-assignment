package kr.co.mz.mzdinterviewassignment.mapper;

import kr.co.mz.mzdinterviewassignment.assembler.ProfileResponseModelAssembler;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberDetailResponseWithProfileModel;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseMapper {

  private final ProfileResponseModelAssembler profileResponseModelAssembler;

  public MemberDetailResponseWithProfileModel generateResponseWithProfileModel(MemberDetailsResponse memberDetailsResponse) {
    return MemberDetailResponseWithProfileModel.builder()
        .memberNo(memberDetailsResponse.getMemberNo())
        .loginId(memberDetailsResponse.getLoginId())
        .name(memberDetailsResponse.getName())
        .password(memberDetailsResponse.getPassword())
        .memberStatus(memberDetailsResponse.getMemberStatus())
        .updatedAt(memberDetailsResponse.getUpdatedAt())
        .createdAt(memberDetailsResponse.getCreatedAt())
        .profiles(profileResponseModelAssembler.toCollectionModel(memberDetailsResponse.getProfiles()))
        .build();
  }
}
