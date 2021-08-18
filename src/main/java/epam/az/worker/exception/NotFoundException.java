package epam.az.worker.exception;

import epam.az.worker.error.ErrorCode;

public class NotFoundException extends BaseException {

    public NotFoundException(ErrorCode errorCode, Object...values) {

        super(errorCode, values);
    }
}
