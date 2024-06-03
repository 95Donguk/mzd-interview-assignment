package kr.co.mz.mzdinterviewassignment.controller.advice;

import jakarta.validation.ConstraintViolationException;
import kr.co.mz.mzdinterviewassignment.dto.response.error.ErrorResponse;
import kr.co.mz.mzdinterviewassignment.exception.member.DuplicateLoginIdException;
import kr.co.mz.mzdinterviewassignment.exception.member.NotFoundMemberException;
import kr.co.mz.mzdinterviewassignment.exception.profile.CannotDeleteProfileException;
import kr.co.mz.mzdinterviewassignment.exception.profile.EmptyProfileException;
import kr.co.mz.mzdinterviewassignment.exception.profile.NonMatchMemberNoException;
import kr.co.mz.mzdinterviewassignment.exception.profile.NotFoundProfileException;
import kr.co.mz.mzdinterviewassignment.exception.status.InvalidStatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
/**
 * @ControllerAdvice: 컨트롤러 안에서 발생하는 예외를 처리하고 관리하는 어노테이션입니다.
 * 대상으로 지정된 컨트롤러에  @ExceptionHandler , @InitBinder(데이터 검증) 기능을 부여해주는 역할을 한다.
 * @ExceptionHandler : 지정한 예외를 발생했을 때 예외 처리하여 적절한 응답을 생성할 수 있다
 * - 우선순위 지정
 * - 다양한 예외 처리
 * - 예외 생략
 */
@RestControllerAdvice(basePackages = "kr.co.mz.mzdinterviewassignment")
public class ApiControllerAdvice {

    /**
     * 서버에서 발생된 예외로 인해 요청을 처리할 수 없음
     * INTERNAL_SERVER_ERROR: 500
     * 클라이언트에게 서버에서 오류가 발생했다는 것만 알려주고 정확한 정보를 제공하지 않음
     * 시스템 내부 정보를 노출시킬 수 있기 때문
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        log.error("Exception message : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .message("요청에 실패했습니다.")
                .build());
    }

    /**
     * 이 예외는 주로 데이터베이스 트랜잭션 중에 엔티티의 상태가 유효성 검사 규칙을 위반했을 때 발생하는 예외
     * BAD_REQUEST: 400, 클라이언트가 유효하지 않은 데이터를 서버로 보낸 경우 서버에서 처리할 수 없다는 의미
     * @return Member, Profile 필드의 NotNull 예외 발생 시 해당 필드의 문제를 클라이언트에게 메시지를 보여주기 위해 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
        ConstraintViolationException e) {

        log.debug("Exception message : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(HttpStatus.BAD_REQUEST.name())
                .message(e.getConstraintViolations().stream()
                    .map(violation -> violation.getPropertyPath() +
                        ": " +
                        violation.getMessage())
                    .toList().toString())
                .build());
    }

    /**
     * @Valid로 주석이 달린 인자의 유효성 검사가 실패했을 때 발생하는 예외
     * 요청 파라미터의 데이터 형식이 잘못 됐거나
     * Bean Validation 으로 설정한 제약조건이 맞지 않을 때 예외가 발생
     * 예외 발생 시 해당 필드의 문제를 클라이언트에게 메시지를 보여주기 위해 처리
     * @return 형식이나 조건이 맞지 않은 파라미터의 문제를 클라이언트에게 메시지를 보여주기 위해 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException e) {

        log.debug("Exception message : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(HttpStatus.BAD_REQUEST.name())
                .message(e.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList().toString())
                .build());
    }

    /**
     * 클라이언트가 보낸 요청의 본문(payload)이 읽을 수 없을 때 발생하는 예외입니다. 
     * 이는 JSON이 잘못 형식화되었거나, 예상되는 타입과 다른 타입의 데이터가 전송되었을 때 발생할 수 있습니다
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException e) {

        log.debug("Exception message : {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(HttpStatus.BAD_REQUEST.name())
                .message("JSON parse error")
                .build());
    }

    /**
     * 회원 생성 시 생성할 아이디가 이미 데이터베이스에 존재하여 회원 생성할 수 없을 때 발생하는 예외 처리 
     * @return "이미 존재하는 아이디 입니다" 라는 메시지를 반환
     */
    @ExceptionHandler(DuplicateLoginIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateLoginIdException(
        DuplicateLoginIdException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(HttpStatus.BAD_REQUEST.name())
                .message(e.getMessage())
                .build());
    }

    /**
     * 요청 URL의 PathVariable 로 들어온 회원 식별 번호로 회원을 조회하거나 삭제를 요청했을 때 데이터베이스에 회원을 찾을 수 없을 때 발생하는 예외 처리
     * NOT_FOUND: 404, 클라이언트가 요청한 리소스를 찾을 수 없다는 의미의 상태 코드
     * @return "회원을 찾을 수 없습니다" 메시지 반환
     */
    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundMemberException(NotFoundMemberException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .code(HttpStatus.NOT_FOUND.name())
                .message(e.getMessage())
                .build());
    }

    /**
     * 회원 조회 시 회원에 프로필 정보가 없을 때 발생하는 예외 처리
     * @return "회원의 프로필이 존재하지 않으니 프로필을 추가해야합니다" 라는 메시지 반환
     */
    @ExceptionHandler(EmptyProfileException.class)
    public ResponseEntity<ErrorResponse> handleEmptyProfileException(EmptyProfileException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(HttpStatus.BAD_REQUEST.name())
                .message(e.getMessage())
                .build());
    }

    /**
     * 요청 URL의 PathVariable 로 들어온 회원 식별 번호와 프로필의 회원 식별 번호가 일치 하지 않을 때 발생하는 예외 처리
     * @return "회원과 프로필이 일치 하지 않습니다." 라는 메시지 반환
     */
    @ExceptionHandler(NonMatchMemberNoException.class)
    public ResponseEntity<ErrorResponse> handleNonMatchMemberNoException(
        NonMatchMemberNoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(HttpStatus.BAD_REQUEST.name())
                .message(e.getMessage())
                .build());
    }

    /**
     * 프로필 삭제나 수정 요청에서 프로필 조회 시 요청 URL의 프로필 식별 번호로 프로필을 찾을 수 없을 때 발생하는 에외 처리
     * @return "프로필을 찾을 수 없습니다." 메시지 반환
     */
    @ExceptionHandler(NotFoundProfileException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundProfileException(NotFoundProfileException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .code(HttpStatus.NOT_FOUND.name())
                .message(e.getMessage())
                .build());
    }

    /**
     * 프로필 수정 시 요청 본문(payload) 프로필 상태 값이 유효하지 않은 상태일 때 발생하는 예외 처리
     * @return "유효하지 않은 프로필 상태입니다." 메시지 반환
     */
    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidStatusException(InvalidStatusException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code(HttpStatus.BAD_REQUEST.name())
                .message(e.getMessage())
                .build());
    }

    /**
     * 프로필 삭제 시, 회원의 프로필이 1개만 존재할 때 프로필을 삭제할 수 없을 때 발생하는 예외 처리
     * CONFLICT: 409, 현재 요청을 수행할 수 없음을 의미, 요청의 세부사항은 메시지로 전달
     * @return "프로필은 최소 1개의 프로필이 있어야하므로 프로필을 삭제 할 수 없습니다."
     */
    @ExceptionHandler(CannotDeleteProfileException.class)
    public ResponseEntity<ErrorResponse> handleCannotDeleteProfileException(CannotDeleteProfileException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .code(HttpStatus.CONFLICT.name())
                .message(e.getMessage())
                .build());
    }
}