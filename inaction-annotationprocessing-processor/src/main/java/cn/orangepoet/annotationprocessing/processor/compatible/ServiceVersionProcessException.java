package cn.orangepoet.annotationprocessing.processor.compatible;


import javax.lang.model.element.Element;

/**
 * 对{@link VersionRoute}注解处理 {@link ServiceVersionAnnotationProcessor} 遇到异常
 */
public class ServiceVersionProcessException extends RuntimeException {

    private Element element;

    public ServiceVersionProcessException(Element element, String msg, Object... args) {
        super(String.format(msg, args));
        this.element = element;
    }

    public ServiceVersionProcessException() {
        super();
    }

    public ServiceVersionProcessException(String message) {
        super(message);
    }

    public ServiceVersionProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceVersionProcessException(Throwable cause) {
        super(cause);
    }

    protected ServiceVersionProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


    public Element getElement() {
        return element;
    }
}
