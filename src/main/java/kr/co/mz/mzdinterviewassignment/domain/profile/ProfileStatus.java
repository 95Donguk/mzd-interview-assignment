package kr.co.mz.mzdinterviewassignment.domain.profile;

import com.fasterxml.jackson.annotation.JsonCreator;
import kr.co.mz.mzdinterviewassignment.exception.status.InvalidStatusException;
import kr.co.mz.mzdinterviewassignment.exception.status.StatusExceptionCode;

public enum ProfileStatus {
    MAIN,
    NORMAL;

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
