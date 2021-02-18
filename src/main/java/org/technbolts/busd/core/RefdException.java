package org.technbolts.busd.core;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RefdException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String message;
    private final Map<String,String> args;

    public RefdException(ErrorCode errorCode, String message, Map<String,String> args) {
        this(errorCode, null, message, args);
    }

    public RefdException(ErrorCode errorCode, Throwable cause, String message, Map<String,String> args) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
        this.args = args;
    }

    public ErrorCode errorCode() {
        return errorCode;
    }

    public String message() {
        return message;
    }

    public Map<String,String> args() {
        return args;
    }
}
