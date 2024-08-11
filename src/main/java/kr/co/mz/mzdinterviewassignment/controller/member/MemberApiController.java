package kr.co.mz.mzdinterviewassignment.controller.member;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import kr.co.mz.mzdinterviewassignment.assembler.MemberDetailResponseModelAssembler;
import kr.co.mz.mzdinterviewassignment.assembler.MemberInfoResponseModelAssembler;
import kr.co.mz.mzdinterviewassignment.dto.request.member.CreateMemberRequest;
import kr.co.mz.mzdinterviewassignment.dto.response.ApiResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberDetailResponseWithProfileModel;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberDetailsResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberInfoResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberResponse;
import kr.co.mz.mzdinterviewassignment.facade.MemberProfileFacade;
import kr.co.mz.mzdinterviewassignment.mapper.ResponseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {

  private final MemberProfileFacade memberProfileFacade;
  private final MemberDetailResponseModelAssembler memberDetailResponseModelAssembler;
  private final MemberInfoResponseModelAssembler memberInfoResponseModelAssembler;
  private final ResponseMapper responseMapper;

  @PostMapping
  public ResponseEntity<ApiResponse<MemberResponse>> createMember(
      @Valid @RequestBody CreateMemberRequest dto) {
    log.info("회원 생성 요청");
    MemberResponse data = memberProfileFacade.createMember(dto);

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{memberNo}")
        .buildAndExpand(data.getMemberNo())
        .toUri();

    ApiResponse<MemberResponse> response = ApiResponse.<MemberResponse>builder()
        .code(HttpStatus.CREATED.name())
        .message("회원 생성 성공")
        .data(data)
        .build();

    return ResponseEntity.created(location).body(response);
  }

  @DeleteMapping("/{memberNo}")
  public ResponseEntity<ApiResponse<String>> deleteMember(
      @PathVariable("memberNo") Long memberNo) {
    log.info("회원 삭제 요청");

    String loginId = memberProfileFacade.deleteMember(memberNo);

    ApiResponse<String> response = ApiResponse.<String>builder()
        .code(HttpStatus.OK.name())
        .message("회원 삭제 성공")
        .data("삭제된 회원 아이디 : " + loginId)
        .build();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{memberNo}")
  @ResponseStatus(HttpStatus.OK)
  public EntityModel<MemberDetailResponseWithProfileModel> findMember(
      @PathVariable("memberNo") Long memberNo) {
    log.info("회원 상세 조회 요청");

    MemberDetailsResponse data = memberProfileFacade.findMemberDetails(memberNo);

    MemberDetailResponseWithProfileModel mapperResult = responseMapper.generateResponseWithProfileModel(
        data);

    return memberDetailResponseModelAssembler.toModel(mapperResult);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public CollectionModel<EntityModel<MemberInfoResponse>> findMembers(
      @RequestParam(name = "page", required = false, defaultValue = "0") int page,
      @RequestParam(name = "size", required = false, defaultValue = "10") int size,
      @RequestParam(name = "name", required = false, defaultValue = "") String name
  ) {
    log.info("회원 전체 조회 요청");

    List<MemberInfoResponse> data = memberProfileFacade.findMembers(page, size, name);

    List<EntityModel<MemberInfoResponse>> memberModels = data.stream()
        .map(memberInfoResponseModelAssembler::toModel)
        .toList();

    return CollectionModel.of(memberModels,
        linkTo(methodOn(MemberApiController.class).findMembers(page, size, name)).withSelfRel(),
        linkTo(methodOn(MemberApiController.class).findMembers(page - 1, size, name)).withRel(
            IanaLinkRelations.PREV),
        linkTo(methodOn(MemberApiController.class).findMembers(page + 1, size, name)).withRel(
            IanaLinkRelations.NEXT));
  }
}
