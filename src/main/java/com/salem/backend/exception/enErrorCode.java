package com.salem.backend.exception;


public enum enErrorCode {
    
    // Auth
    AUTH_FAILED("error.auth.failed"),
    USER_NOT_FOUND("error.user.not.found"),
    USER_ALREADY_EXISTS("error.user.exists"),
    
    // Notes
    NOTE_NOT_FOUND("error.note.not.found"),
    NOTE_OWNERSHIP_ERROR("error.note.ownership"),
    
    // System (General keys for global handler)
    INTERNAL_ERROR("error.system.internal"),
    VALIDATION_ERROR("error.validation.general");

    private final String messageKey;

    enErrorCode(String messageKey) {
        this.messageKey = messageKey;
    }
    public String getMessageKey() {
        return messageKey;
    }
}