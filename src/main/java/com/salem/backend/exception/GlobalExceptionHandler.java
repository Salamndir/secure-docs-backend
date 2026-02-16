package com.salem.backend.exception;

import com.salem.backend.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    // 1. Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String key = error.getDefaultMessage();
            
            // Clean up curly braces {}
            if (key != null && key.startsWith("{") && key.endsWith("}")) {
                key = key.substring(1, key.length() - 1);
            }
            
            // Translate
            String msg = messageSource.getMessage(key, error.getArguments(), key, LocaleContextHolder.getLocale());
            errors.put(error.getField(), msg);
        }

        // Use the Enum to get the general message key "error.validation.general"
        String generalMessage = getMessage(enErrorCode.VALIDATION_ERROR.getMessageKey());

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .code(enErrorCode.VALIDATION_ERROR.name())
                        .message(generalMessage)
                        .fieldErrors(errors)
                        .build()
        );
    }

    // 2. Business Logic Errors
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        // Read the key directly from the Enum: ex.getErrorCode().getMessageKey()
        String msg = getMessage(ex.getErrorCode().getMessageKey());

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .code(ex.getErrorCode().name()) // Returns e.g., "NOTE_NOT_FOUND"
                        .message(msg)                   // Returns translated text
                        .build()
        );
    }

    // 3. General System Errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unhandled Exception: ", ex); // Log full trace internally

        // Use the Enum for Internal Error key
        String msg = getMessage(enErrorCode.INTERNAL_ERROR.getMessageKey());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse.builder()
                        .code(enErrorCode.INTERNAL_ERROR.name())
                        .message(msg)
                        .build()
        );
    }

    // Helper method to shorten code
    private String getMessage(String key) {
        return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
    }
}