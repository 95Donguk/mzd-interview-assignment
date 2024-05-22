package kr.co.mz.mzdinterviewassignment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
public non-sealed class ApiResponse<T> extends Response {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final T data;

  @Builder
  private ApiResponse(final String code, final String message, final T data) {
    super(code, message);
    this.data = data;
  }
}
