package com.gary.exception;

/**
 * describe:
 *
 * @author gary
 * @date 2019/01/12
 */
public class PortNotDefinedException extends Exception {
    public PortNotDefinedException() {
        super();
    }

    public PortNotDefinedException(String message) {
        super(message);
    }

    public PortNotDefinedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PortNotDefinedException(Throwable cause) {
        super(cause);
    }

    protected PortNotDefinedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
