package com.experiment.exception;

/**
 * 练习相关异常
 */
public class PracticeException extends RuntimeException {
    
    public PracticeException(String message) {
        super(message);
    }
    
    public PracticeException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public PracticeException(Throwable cause) {
        super(cause);
    }
} 