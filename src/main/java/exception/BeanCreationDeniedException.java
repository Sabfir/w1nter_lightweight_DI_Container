package exception;

public class BeanCreationDeniedException extends Exception {

    public BeanCreationDeniedException(String message) {
        super(message);
    }
    public BeanCreationDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
    public BeanCreationDeniedException(Throwable cause) {
        super(cause);
    }
    public BeanCreationDeniedException(String message, Throwable cause,
                                       boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
