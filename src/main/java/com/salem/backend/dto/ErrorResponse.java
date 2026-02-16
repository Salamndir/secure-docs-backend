package com.salem.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON
public class ErrorResponse {
    
    // The enum code (e.g., "VALIDATION_ERROR", "INTERNAL_ERROR")
    private String code;
    
    // The translated message for the user
    private String message;
    
    // Detailed field errors (only for validation issues)
    private Map<String, String> fieldErrors;
}