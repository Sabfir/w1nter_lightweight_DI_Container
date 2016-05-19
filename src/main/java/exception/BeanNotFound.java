package exception;

public class BeanNotFound extends Exception {

    public BeanNotFound(String message) {
        super(message);
    }
    public BeanNotFound(String message, Throwable cause) {
        super(message, cause);
    }
    public BeanNotFound(Throwable cause) {
        super(cause);
    }
    public BeanNotFound(String message, Throwable cause,
                           boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
