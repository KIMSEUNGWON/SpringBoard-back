package hello.rest.advice.exception;

public class CNotOwnerException extends RuntimeException {
    public CNotOwnerException() {
        super();
    }

    public CNotOwnerException(String message) {
        super(message);
    }

    public CNotOwnerException(String message, Throwable cause) {
        super(message, cause);
    }
}
