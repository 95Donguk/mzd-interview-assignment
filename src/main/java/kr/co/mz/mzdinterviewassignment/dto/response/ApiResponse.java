package kr.co.mz.mzdinterviewassignment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * - 자바 제네릭
 *      - 타입 안정성: 컴파일 시점에 타입 검사를 수행하여 잘못된 데이터유형으로 인한 런타임 에러 방지
 *      - 코드 재사용성: 동일한 코드를 여러 다른 데이터 유형에 사용할 수 있음
 */
@Getter
@JsonPropertyOrder({"code", "message", "data"})
@RequiredArgsConstructor
public class ApiResponse<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

}
