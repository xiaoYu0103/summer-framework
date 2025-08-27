package org.yxw.exception;

public class NestedRuntimeException extends RuntimeException {

    public NestedRuntimeException() {
        super();
    }

    public NestedRuntimeException(String msg) {
        super(msg);
    }

    public NestedRuntimeException(Throwable cause) {
        super(cause);
    }

    public NestedRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
