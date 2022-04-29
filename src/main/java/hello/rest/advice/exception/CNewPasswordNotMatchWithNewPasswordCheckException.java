package hello.rest.advice.exception;

public class CNewPasswordNotMatchWithNewPasswordCheckException extends RuntimeException {
    public CNewPasswordNotMatchWithNewPasswordCheckException() {
        super();
    }

    public CNewPasswordNotMatchWithNewPasswordCheckException(String message) {
        super(message);
    }

    public CNewPasswordNotMatchWithNewPasswordCheckException(String message, Throwable cause) {
        super(message, cause);
    }
}
