package kr.co.mz.mzdinterviewassignment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
public final class ErrorResponse extends Response {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final int status;

  @Builder
  private ErrorResponse(final String code, final String message, final int status) {
    super(code, message);
    this.status = status;
  }
}
