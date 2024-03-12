package kr.co.mz.mzdinterviewassignment.exception.status;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException(final StatusExceptionCode code, final String status) {
        super(code.getMessage());
        log.error("{} Invalid status: {}", code.getMessage(), status);
    }
}
