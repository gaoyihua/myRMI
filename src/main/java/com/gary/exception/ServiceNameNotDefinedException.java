package com.gary.exception;

/**
 * describe:
 *
 * @author gary
 * @date 2019/01/12
 */
public class ServiceNameNotDefinedException extends Exception {
    public ServiceNameNotDefinedException() {
        super();
    }

    public ServiceNameNotDefinedException(String message) {
        super(message);
    }

    public ServiceNameNotDefinedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceNameNotDefinedException(Throwable cause) {
        super(cause);
    }

    protected ServiceNameNotDefinedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
