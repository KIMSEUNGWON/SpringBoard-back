package hello.rest.advice.exception;

public class CPostNotInBoardException extends RuntimeException {
    public CPostNotInBoardException() {
        super();
    }

    public CPostNotInBoardException(String message) {
        super(message);
    }

    public CPostNotInBoardException(String message, Throwable cause) {
        super(message, cause);
    }
}
