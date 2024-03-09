package kr.co.mz.mzdinterviewassignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRequest {

    @NotBlank(message = "아이디를 입력해주세요")
    @Pattern(regexp = "^[a-z\\d]{4,20}$",
        message = "아이디는 영문 소문자와 숫자로 구성된 4 ~ 12자리로 입력해주세요.")
    private String loginId;

    @NotBlank(message = "이름을 입력해주세요")
    @Pattern(regexp = "^[가-힣]{2,8}$", message = "이름은 한글 2 ~ 8자리로 입력해주세요.")
    private String name;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$",
        message = "비밀번호는 영문 대소문자, 숫자, 특수문자'~!@#$%^&*()+|='의 8 ~ 16자리로 입력해주세요.")
    private String password;
}
