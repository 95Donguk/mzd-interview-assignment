package kr.co.mz.mzdinterviewassignment.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import kr.co.mz.mzdinterviewassignment.controller.member.MemberApiController;
import kr.co.mz.mzdinterviewassignment.controller.profile.ProfileApiController;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberInfoResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class MemberInfoResponseModelAssembler implements
    RepresentationModelAssembler<MemberInfoResponse, EntityModel<MemberInfoResponse>> {

  @Override
  public EntityModel<MemberInfoResponse> toModel(MemberInfoResponse memberInfo) {
    return EntityModel.of(memberInfo,
        linkTo(methodOn(MemberApiController.class).findMember(memberInfo.getMemberNo()))
            .withSelfRel(),
        linkTo(methodOn(ProfileApiController.class).findProfile(
            memberInfo.getMainProfile().getProfileNo()))
            .withRel("main-profile"));
  }
}
