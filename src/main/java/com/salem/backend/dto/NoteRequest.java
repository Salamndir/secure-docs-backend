package com.salem.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Java Record: Immutable data carrier
public record NoteRequest(
    
    // The message here refers to the key in messages.properties
    @NotBlank(message = "{validation.title.required}")
    String title,

    // Added size validation to match our properties file
    @Size(min = 10, message = "{validation.content.min}")
    String content
) {}