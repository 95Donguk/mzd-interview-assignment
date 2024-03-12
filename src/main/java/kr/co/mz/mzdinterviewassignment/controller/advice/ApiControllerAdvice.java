package kr.co.mz.mzdinterviewassignment.controller.advice;

import jakarta.validation.ConstraintViolationException;
import java.util.Optional;
import kr.co.mz.mzdinterviewassignment.dto.response.error.ErrorResult;
import kr.co.mz.mzdinterviewassignment.exception.member.DuplicateLoginIdException;
import kr.co.mz.mzdinterviewassignment.exception.member.NotFoundMemberException;
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
@RestControllerAdvice(basePackages = "kr.co.mz.mzdinterviewassignment")
public class ApiControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> handleException(Exception e) {
        log.error("Exception catch : ", e);
        log.error("Exception catch : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResult.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .message("요청에 실패했습니다.")
                .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResult> handleConstraintViolationException(
        ConstraintViolationException e) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResult.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .message(e.getConstraintViolations().stream()
                    .map(violation -> violation.getPropertyPath() +
                        ": " +
                        violation.getMessage())
                    .toList().toString())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResult> handleValidationException(
        MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResult.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .message(e.getFieldErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList().toString())
                .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResult> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResult.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .message("JSON parse error")
                .build());
    }

    @ExceptionHandler(DuplicateLoginIdException.class)
    public ResponseEntity<ErrorResult> handleDuplicateLoginIdException(
        DuplicateLoginIdException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResult.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(NotFoundMemberException.class)
    public ResponseEntity<ErrorResult> handleNotFoundMemberException(NotFoundMemberException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResult.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.name())
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(EmptyProfileException.class)
    public ResponseEntity<ErrorResult> handleEmptyProfileException(EmptyProfileException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResult.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(NonMatchMemberNoException.class)
    public ResponseEntity<ErrorResult> handleNonMatchMemberNoException(
        NonMatchMemberNoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResult.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(NotFoundProfileException.class)
    public ResponseEntity<ErrorResult> handleNotFoundProfileException(NotFoundProfileException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResult.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.name())
                .message(e.getMessage())
                .build());
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ErrorResult> handleInvalidStatusException(InvalidStatusException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResult.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.name())
                .message(e.getMessage())
                .build());
    }
}