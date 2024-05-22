package kr.co.mz.mzdinterviewassignment.controller.member;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import kr.co.mz.mzdinterviewassignment.dto.request.member.CreateMemberRequest;
import kr.co.mz.mzdinterviewassignment.dto.response.ApiResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberDetailsResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberInfoResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.member.MemberResponse;
import kr.co.mz.mzdinterviewassignment.facade.MemberProfileFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberProfileFacade memberProfileFacade;

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
    public ResponseEntity<ApiResponse<MemberDetailsResponse>> findMember(
        @PathVariable("memberNo") Long memberNo) {
        log.info("회원 상세 조회 요청");

        MemberDetailsResponse data = memberProfileFacade.findMemberDetails(memberNo);

        ApiResponse<MemberDetailsResponse> response = ApiResponse.<MemberDetailsResponse>builder()
            .code(HttpStatus.OK.name())
            .message("회원 상세 조회 성공")
            .data(data)
            .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberInfoResponse>>> findMembers(
        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @RequestParam(name = "size", required = false, defaultValue = "10") int size,
        @RequestParam(name = "name", required = false, defaultValue = "") String name
    ) {
        log.info("회원 전체 조회 요청");

        List<MemberInfoResponse> data = memberProfileFacade.findMembers(page, size, name);

        ApiResponse<List<MemberInfoResponse>> response = ApiResponse.<List<MemberInfoResponse>>builder()
            .code(HttpStatus.OK.name())
            .message("회원 전체 조회 성공")
            .data(data)
            .build();

        return ResponseEntity.ok(response);
    }
}
