package org.yxw.exception;

public class BeanException extends NestedRuntimeException {
    BeanException() {
        super();
    }

    BeanException(String msg) {
        super(msg);
    }

    BeanException(Throwable cause) {
        super(cause);
    }

    BeanException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
