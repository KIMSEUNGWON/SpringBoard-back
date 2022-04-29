package hello.rest.advice.exception;

public class CForbiddenWordException extends RuntimeException {
    public CForbiddenWordException() {
        super();
    }

    public CForbiddenWordException(String message) {
        super(message);
    }

    public CForbiddenWordException(String message, Throwable cause) {
        super(message, cause);
    }
}
