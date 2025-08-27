package org.yxw.exception;

public class BeanCreationException extends BeanException{
    public BeanCreationException() {
        super();
    }

    public BeanCreationException(String msg) {
        super(msg);
    }

    public BeanCreationException(Throwable cause) {
        super(cause);
    }

    public BeanCreationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
