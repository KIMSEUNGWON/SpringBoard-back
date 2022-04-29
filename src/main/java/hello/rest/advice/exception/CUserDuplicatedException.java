package hello.rest.advice.exception;

public class CUserDuplicatedException extends RuntimeException {
    public CUserDuplicatedException() {
        super();
    }

    public CUserDuplicatedException(String message) {
        super(message);
    }

    public CUserDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
