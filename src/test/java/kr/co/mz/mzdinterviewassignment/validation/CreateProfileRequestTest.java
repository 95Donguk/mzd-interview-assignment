package kr.co.mz.mzdinterviewassignment.validation;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import kr.co.mz.mzdinterviewassignment.dto.request.profile.CreateProfileRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateProfileRequestTest {

  private static ValidatorFactory factory;
  private static Validator validator;

  @BeforeAll
  public static void init() {
    factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("허용하지 않는 닉네임 유효성 검사")
  void fail_validate_nickname_test() {
    CreateProfileRequest request = new CreateProfileRequest("홍!길@동#", "01000000000",
        "부산광역시 해운대구 재송동 1012-1");
    Set<ConstraintViolation<CreateProfileRequest>> violations = validator.validate(request);

    assertThat(violations).isNotEmpty();
    assertThat(violations.stream()
        .anyMatch(violation -> violation.getMessage()
            .equals("닉네임은 영문 대소문자, 숫자, 한글로 구성된 2 ~ 8자리로 입력해주세요."))).isTrue();
  }

  @Test
  @DisplayName("허용하지 않는 닉네임 유효성 검사")
  void fail_validate_phoneNumber_test() {
    CreateProfileRequest request = new CreateProfileRequest("홍!길@동#", "010-1234-5678",
        "부산광역시 해운대구 재송동 1012-1");
    Set<ConstraintViolation<CreateProfileRequest>> violations = validator.validate(request);

    assertThat(violations).isNotEmpty();
    assertThat(violations.stream()
        .anyMatch(violation -> violation.getMessage()
            .equals("휴대전화 번호는 하이픈(-)을 제외한 10자리 또는 11자리로 입력해주세요."))).isTrue();
  }

  @Test
  @DisplayName("유효한 프로필 정보 검사")
  void success_validate_profileInfo_Test() {
    CreateProfileRequest request = new CreateProfileRequest("홍길동", "01000000000",
        "부산광역시 해운대구 재송동 1012-1");
    Set<ConstraintViolation<CreateProfileRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }
}
