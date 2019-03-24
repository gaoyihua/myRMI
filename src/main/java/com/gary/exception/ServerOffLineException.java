package com.gary.exception;

/**
 * describe:
 *
 * @author gary
 * @date 2019/01/12
 */
public class ServerOffLineException extends Exception {
    public ServerOffLineException() {
        super();
    }

    public ServerOffLineException(String message) {
        super(message);
    }

    public ServerOffLineException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerOffLineException(Throwable cause) {
        super(cause);
    }

    protected ServerOffLineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
