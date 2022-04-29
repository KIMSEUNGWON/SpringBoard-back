package hello.rest.advice.exception;

public class CUserPasswordNotMatchException extends RuntimeException {
    public CUserPasswordNotMatchException() {
        super();
    }

    public CUserPasswordNotMatchException(String message) {
        super(message);
    }

    public CUserPasswordNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
