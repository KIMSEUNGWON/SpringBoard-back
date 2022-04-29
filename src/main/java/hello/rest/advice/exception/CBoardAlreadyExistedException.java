package hello.rest.advice.exception;

public class CBoardAlreadyExistedException extends RuntimeException {
    public CBoardAlreadyExistedException() {
        super();
    }

    public CBoardAlreadyExistedException(String message) {
        super(message);
    }

    public CBoardAlreadyExistedException(String message, Throwable cause) {
        super(message, cause);
    }
}
