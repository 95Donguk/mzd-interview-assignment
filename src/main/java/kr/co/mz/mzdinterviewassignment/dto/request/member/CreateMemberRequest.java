package kr.co.mz.mzdinterviewassignment.dto.request.member;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import kr.co.mz.mzdinterviewassignment.domain.member.Member;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.CreateProfileRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CreateMemberRequest {

    @Pattern(regexp = "^[a-z\\d]{4,20}$",
        message = "아이디는 영문 소문자와 숫자로 구성된 4 ~ 12자리로 입력해주세요.")
    @NotNull(message = "로그인 아이디는 필수입니다")
    private String loginId;

    @Pattern(regexp = "^[가-힣]{2,8}$", message = "이름은 한글로 구성된 2 ~ 8자리로 입력해주세요.")
    @NotNull(message = "이름은 필수입니다")
    private String name;

    @Pattern(regexp = "^[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$",
        message = "비밀번호는 영문 대소문자, 숫자, 특수문자'~!@#$%^&*()+|='로 구성된 8 ~ 16자리로 입력해주세요.")
    @NotNull(message = "비밀번호는 필수입니다")
    private String password;

    @Valid
    @NotNull(message = "프로필은 필수입니다.")
    private CreateProfileRequest profile;

    public Member toEntity() {
        return Member.builder()
            .loginId(loginId)
            .name(name)
            .password(password)
            .build();
    }
}
