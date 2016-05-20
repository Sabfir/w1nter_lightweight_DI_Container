package exception;

public class BeanNotFound extends Exception {

    public BeanNotFound(final String message) {
        super(message);
    }
    public BeanNotFound(final String message, final Throwable cause) {
        super(message, cause);
    }
    public BeanNotFound(final Throwable cause) {
        super(cause);
    }
    public BeanNotFound(final String message, final Throwable cause,
                        final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
