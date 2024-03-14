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

/**
 * RestController 안에 @ResponseBody가 있는데
 * @ResponseBody는 컨트롤러 메서드가 반환하는 객체를 HTTP 응답 BODY로 전송하는데 사용합니다.
 * 스프링 컨텍스트에 빈으로 등록하여 컨트롤러로 사용
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberProfileFacade memberProfileFacade;

    /**
     * Post: 클라이언트가 서버에 데이터을 전송할 때 사용합니다.
     * - 리소스를 생성할 때 사용됩니다.
     * - @Valid request dto 안의 Bean Validation 유효성 검사
     * - @RequestBody 는 HTTP 요청 바디를 파라미터로 매핑할 때 사용 (HttpMessageConverter)
     * - 자바 제네릭
     *      - 타입 안정성: 컴파일 시점에 타입 검사를 수행하여 잘못된 데이터유형으로 인한 런타임 에러 방지
     *      - 코드 재사용성: 동일한 코드를 여러 다른 데이터 유형에 사용할 수 있음
     */
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> createMember(
        @Valid @RequestBody CreateMemberRequest dto) {
        log.info("회원 생성 요청");
        MemberResponse data = memberProfileFacade.createMember(dto);

        /**
         * location
         * - HTTP HEADER 의 Key
         * - 클라이언트에게 생성된 회원 리소스는 location에 담은 uri로 접근이 가능하다는 것을 알려주기 위함
         */
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{memberNo}")
            .buildAndExpand(data.getMemberNo())
            .toUri();

        /**
         * @return
         * 상태 코드 이름
         * 메시지
         * 데이터
         */
        ApiResponse<MemberResponse> response = new ApiResponse<>(HttpStatus.CREATED.name(),
            "회원 생성 성공",
            data);

        return ResponseEntity.created(location).body(response);
    }

    /**
     * DELETE : 서버에서 특정 리소스를 삭제하기 위해 사용
     * @PathVariable : URL 경로에서 특정 변수를 추출하여 메서드의 매개변수에 할당하는 데 사용
     * @return
     */
    @DeleteMapping("/{memberNo}")
    public ResponseEntity<ApiResponse<String>> deleteMember(
        @PathVariable("memberNo") Long memberNo) {
        log.info("회원 삭제 요청");

        String loginId = memberProfileFacade.deleteMember(memberNo);

        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK.name(),
            "회원 삭제 성공",
            "삭제된 회원 아이디 : " + loginId);

        /**
         * 200번(OK)로 반환되지만 204(No Content)로 반환 할 수도 있다.
         * 204는 콘텐츠를 반환하지 않는다는 의미로
         * 응답이 필요하지 않은 요청일 때 사용
         */
        return ResponseEntity.ok(response);
    }

    /**
     * Get : 서버에서 특정 리소스를 조회하기 위해 사용
     * ResponseEntity<ApiResponse<MemberDetailsResponse>> 방식으로 반환한 이유는?
     * - 응답 데이터를 어떻게 보여줄지에 대한 고민이 끝에 이 방식을 채택했다.
     * - 처음 구현 시에는 비즈니스 로직이 반환된 값만 body에 넣어서 응답을 줬는데
     * - 막상 응답 데이터를 보니 데이터만 보여지는게 이 데이터가 무엇을 의미하는 건지 추측하게 됐다.
     * - 그래서 응답 데이터에 http 상태와 메시지를 보여주면 좋을 것 같다는 생각에
     * - ApiResponse 라는 클라이언트와의 원활한 협업, 그리고 공통 응답을 관리하는 커스텀 객체를 만들어서 응답 데이터를 줬는데
     * - HTTP 헤더에도 정보를 담아서 보내주고 싶다라는 고민 끝에 이런 방식으로 반환했습니다.
     * - 실제로 실무에서는 이 고민이 있을 때 어떻게 반환하는지 알고 싶습니다.
     */
    @GetMapping("/{memberNo}")
    public ResponseEntity<ApiResponse<MemberDetailsResponse>> findMember(
        @PathVariable("memberNo") Long memberNo) {
        log.info("회원 상세 조회 요청");

        MemberDetailsResponse data = memberProfileFacade.findMemberDetails(memberNo);

        ApiResponse<MemberDetailsResponse> response = new ApiResponse<>(HttpStatus.OK.name(),
            "회원 상세 조회 성공",
            data);

        return ResponseEntity.ok(response);
    }

    /**
     * @Requestparam 쿼리 파라미터를 메서드의 파라미터로 바인딩하는데 사용됩니다.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberInfoResponse>>> findMembers(
        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @RequestParam(name = "size", required = false, defaultValue = "10") int size,
        @RequestParam(name = "name", required = false, defaultValue = "") String name
    ) {
        log.info("회원 전체 조회 요청");

        List<MemberInfoResponse> data = memberProfileFacade.findMembers(page, size, name);

        ApiResponse<List<MemberInfoResponse>> response = new ApiResponse<>(HttpStatus.OK.name(),
            "회원 전체 조회 성공",
            data);

        return ResponseEntity.ok(response);
    }
}
