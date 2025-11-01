package com.cha.markit.exception;

import lombok.Getter;

@Getter
public class InvalidImageException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public InvalidImageException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public InvalidImageException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }
}
