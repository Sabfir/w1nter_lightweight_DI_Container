package exception;

public class BeanCreationDeniedException extends Exception {

    public BeanCreationDeniedException(final String message) {
        super(message);
    }
    public BeanCreationDeniedException(final String message, final Throwable cause) {
        super(message, cause);
    }
    public BeanCreationDeniedException(final Throwable cause) {
        super(cause);
    }
    public BeanCreationDeniedException(final String message, final Throwable cause,
                                       final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
