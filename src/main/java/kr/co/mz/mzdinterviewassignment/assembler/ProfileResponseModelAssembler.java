package kr.co.mz.mzdinterviewassignment.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import kr.co.mz.mzdinterviewassignment.controller.profile.ProfileApiController;
import kr.co.mz.mzdinterviewassignment.dto.response.profile.ProfileResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

// TODO: 링크 생성 간소화
@Component
public class ProfileResponseModelAssembler implements
    RepresentationModelAssembler<ProfileResponse, EntityModel<ProfileResponse>> {

  @Override
  public EntityModel<ProfileResponse> toModel(ProfileResponse profile) {
    return EntityModel.of(profile,
        linkTo(methodOn(ProfileApiController.class).findProfile(
            profile.getProfileNo())).withSelfRel());
  }
}
