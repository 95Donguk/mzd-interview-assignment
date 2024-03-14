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
/**
 * API 엔드 포인트와 일반적인 웹 페이지 엔드포인트를 명확하게 구분하기 위한 것입니다.
 * 1. 네임스페이스 분리
 *     - `/api` 를 사용하여 API 엔드포인트를 다른 엔드포인트와 구분하면, 다른 종류의 요청과 충돌을 방지하고 API 리소스를 위한 네임스페이스를 분리할 수 있습니다.
 * 2. 미래의 확장성
 *     - `api`를 사용함으로써, 향후에 웹 애플리케이션의 다른 부분이나 마이크로서비스와 같은 추가적인 엔드포인트를 도입할 때 확장성을 가질 수 있습니다.
 *
 * Rest API URI를 이런 방식으로 지정한 이유?
 * - 프로필이 회원의 하위 리소스라고 생각하고 이 방식을 채택했습니다.
 * - 다른 방식은 생각해본적 있는가? 요청 쿼리 파라미터에
 */
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

        ApiResponse<ProfileResponse> response = new ApiResponse<>(HttpStatus.CREATED.name(),
            "회원 프로필 생성 성공",
            data);

        return ResponseEntity.created(location).body(response);
    }

    /**
     * 둘 다 리소스를 업데이트하는데 사용되는 메서드입니다.
     * PATCH: 리소스를 일부를 수정하는데 사용하는 메서드로 데이터 형식에 따라 리소스 일부를 수정하고 나머지는 변경하지 않습니다.
     * PUT: 리소스 전체를 수정하는데 사용하는 메서드로 리소스 일부만 전달하게 된다면 일부만 반영되거나 나머지는 없어집니다.
     */
    @PatchMapping("/{profileNo}")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
        @PathVariable("memberNo") Long memberNo,
        @PathVariable("profileNo") Long profileNo,
        @Valid @RequestBody
        UpdateProfileRequest dto) {
        log.info("회원 프로필 수정 요청");

        ProfileResponse data = memberProfileFacade.updateProfile(dto, profileNo, memberNo);

        ApiResponse<ProfileResponse> response = new ApiResponse<>(HttpStatus.OK.name(),
            "회원 프로필 수정 성공",
            data);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{profileNo}")
    public ResponseEntity<ApiResponse<String>> deleteProfile(
        @PathVariable("memberNo") Long memberNo,
        @PathVariable("profileNo") Long profileNo) {
        log.info("회원 프로필 삭제 요청");

        String nickname = memberProfileFacade.deleteProfile(profileNo, memberNo);

        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.name(),
            "회원 프로필 삭제 성공",
            "삭제된 프로필 닉네임 : " + nickname);

        return ResponseEntity.ok(response);
    }
}
