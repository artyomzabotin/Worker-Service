package epam.az.worker.exception;

import epam.az.worker.error.ErrorCode;

public class DateFormatException extends BaseException {

    public DateFormatException(ErrorCode errorCode, Object... values) {
        super(errorCode, values);
    }
}
