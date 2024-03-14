package kr.co.mz.mzdinterviewassignment.domain.profile;

import com.fasterxml.jackson.annotation.JsonCreator;
import kr.co.mz.mzdinterviewassignment.exception.status.InvalidStatusException;
import kr.co.mz.mzdinterviewassignment.exception.status.StatusExceptionCode;

public enum ProfileStatus {
    MAIN,
    NORMAL;

    /**
     * 역직렬화 시 Json으로 들어온 값이 ENUM의 상수와 값이 같은지 확인하고 값이 같다면 ENUM 타입 반환하고
     * 아니라면 유효하지 않은 상태 에러 처리 하게 함
     */
    @JsonCreator
    public static ProfileStatus from(String status) {
        for (ProfileStatus profileStatus : ProfileStatus.values()) {
            if (profileStatus.name().equals(status)) {
                return profileStatus;
            }
        }
        throw new InvalidStatusException(StatusExceptionCode.INVALID_PROFILE, status);
    }
}
