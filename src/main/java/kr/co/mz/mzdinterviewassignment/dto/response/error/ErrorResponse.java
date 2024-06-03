package kr.co.mz.mzdinterviewassignment.dto.response.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * 직렬화 시 Getter를 이용하기 때문입니다.
 */
@Getter
/**
 * JSON 직렬화시 속성의 순서를 명시적으로 지정하는데 사용합니다.
 * 일관된 순서로 가독성을 높일 수 있습니다. 문서를 작성할 수 있습니다.
 * JSON 응답이 변경되더라도 클라이언트 측의 코드 변경을 최소화하여 호환성이 유지가 가능합니다.
 */
@JsonPropertyOrder({"code", "message", "status"})
/**
 * 빌더를 사용하기 위해 사용했습니다.
 * 빌더는 기본 생성자를 사용하고 모든 필드를 주입받을 수 있도록 구현 했습니다.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ErrorResponse {
    /**
     * 객체를 직렬화 시 특정 속성이 null인 경우 해당 속성을 제외하도록 지정
     * null인 경우 응답 데이터에서는 보이지 않습니다.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final int status;
}
