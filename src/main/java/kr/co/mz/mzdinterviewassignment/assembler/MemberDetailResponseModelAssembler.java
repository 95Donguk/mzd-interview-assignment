package kr.co.mz.mzdinterviewassignment.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import kr.co.mz.mzdinterviewassignment.controller.member.MemberApiController;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberDetailResponseWithProfileModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class MemberDetailResponseModelAssembler implements
    RepresentationModelAssembler<MemberDetailResponseWithProfileModel, EntityModel<MemberDetailResponseWithProfileModel>> {

  @Override
  public EntityModel<MemberDetailResponseWithProfileModel> toModel(
      MemberDetailResponseWithProfileModel memberDetail) {

    return EntityModel.of(memberDetail,
        linkTo(methodOn(MemberApiController.class).findMember(memberDetail.getMemberNo()))
            .withSelfRel());
  }
}