package com.salem.backend.exception;


public class BusinessException extends RuntimeException {
    
    private final enErrorCode errorCode;

    public BusinessException(enErrorCode errorCode) {
        super(errorCode.getMessageKey());
        this.errorCode = errorCode;
    }

    public enErrorCode getErrorCode() {
        return errorCode;
    }
}