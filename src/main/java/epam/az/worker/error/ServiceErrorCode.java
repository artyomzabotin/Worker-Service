package epam.az.worker.error;

public enum ServiceErrorCode implements ErrorCode {

    WORKER_NOT_FOUND,
    INVALID_DATE;

    @Override
    public String getCode() {

        return name();
    }
}
