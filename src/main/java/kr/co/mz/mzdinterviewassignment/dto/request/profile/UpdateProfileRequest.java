package kr.co.mz.mzdinterviewassignment.dto.request.profile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import kr.co.mz.mzdinterviewassignment.domain.profile.Profile;
import kr.co.mz.mzdinterviewassignment.domain.profile.ProfileStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UpdateProfileRequest {
    @Pattern(
        regexp = "^[가-힣a-zA-Z0-9-_]{2,8}$",
        message = "닉네임은 영문 대소문자, 숫자, 한글로 구성된 2 ~ 8자리로 입력해주세요."
    )
    @NotNull(message = "별명은 필수입니다")
    private String nickname;

    @Pattern(regexp = "^01(?:0|1|[6-9])(?:\\d{7}|\\d{8})$",
        message = "휴대전화 번호는 하이픈(-)을 제외한 10자리 또는 11자리로 입력해주세요.")
    @NotNull(message = "휴대전화 번호는 필수입니다")
    private String phoneNumber;

    private String address;

    @NotNull(message = "프로필 상태는 필수입니다")
    private ProfileStatus profileStatus;
}
