package cn.orangepoet.annotationprocessing.processor.compatible;

/**
 * 找不到对应版本的处理者异常
 */
public class ServiceVersionNoMatchedException extends RuntimeException {
    public ServiceVersionNoMatchedException() {
        super();
    }

    public ServiceVersionNoMatchedException(String message) {
        super(message);
    }

    public ServiceVersionNoMatchedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceVersionNoMatchedException(Throwable cause) {
        super(cause);
    }

    protected ServiceVersionNoMatchedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
