package epam.az.worker.exception;

import epam.az.worker.error.ErrorCode;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] values;

    public BaseException(ErrorCode errorCode, Object...values) {

        this.errorCode = errorCode;
        this.values = values;
    }

    public BaseException(Throwable cause, ErrorCode errorCode, Object...values) {

        super(cause);
        this.errorCode = errorCode;
        this.values = values;
    }
}
