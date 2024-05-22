package kr.co.mz.mzdinterviewassignment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@JsonPropertyOrder({"code", "message"})
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
abstract sealed class Response permits ApiResponse, ErrorResponse {

  @JsonInclude(Include.NON_NULL)
  private final String code;

  @JsonInclude(Include.NON_NULL)
  private final String message;
}
