package kr.co.mz.mzdinterviewassignment.controller.profile;

import jakarta.validation.Valid;
import java.net.URI;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.CreateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.UpdateProfileRequest;
import kr.co.mz.mzdinterviewassignment.dto.response.ApiResponse;
import kr.co.mz.mzdinterviewassignment.dto.response.profile.ProfileResponse;
import kr.co.mz.mzdinterviewassignment.facade.MemberProfileFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/{memberNo}/profiles")
public class ProfileApiController {

    private final MemberProfileFacade memberProfileFacade;

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile(
        @PathVariable("memberNo") Long memberNo,
        @Valid @RequestBody
        CreateProfileRequest dto) {
        log.info("회원 프로필 생성 요청");

        ProfileResponse data = memberProfileFacade.createProfile(dto, memberNo);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{profileNo}")
            .buildAndExpand(data.getProfileNo())
            .toUri();

        ApiResponse<ProfileResponse> response = ApiResponse.<ProfileResponse>builder()
            .code(HttpStatus.CREATED.name())
            .message("회원 프로필 생성 성공")
            .data(data)
            .build();

        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping("/{profileNo}")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
        @PathVariable("memberNo") Long memberNo,
        @PathVariable("profileNo") Long profileNo,
        @Valid @RequestBody
        UpdateProfileRequest dto) {
        log.info("회원 프로필 수정 요청");

        ProfileResponse data = memberProfileFacade.updateProfile(dto, profileNo, memberNo);

        ApiResponse<ProfileResponse> response = ApiResponse.<ProfileResponse>builder()
            .code(HttpStatus.OK.name())
            .message("회원 프로필 수정 성공")
            .data(data)
            .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{profileNo}")
    public ResponseEntity<ApiResponse<String>> deleteProfile(
        @PathVariable("memberNo") Long memberNo,
        @PathVariable("profileNo") Long profileNo) {
        log.info("회원 프로필 삭제 요청");

        String nickname = memberProfileFacade.deleteProfile(profileNo, memberNo);

        ApiResponse<String> response = ApiResponse.<String>builder()
            .code(HttpStatus.OK.name())
            .message("회원 프로필 삭제 성공")
            .data("삭제된 프로필 닉네임 : " + nickname)
            .build();

        return ResponseEntity.ok(response);
    }
}