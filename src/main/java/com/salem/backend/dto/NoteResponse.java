package com.salem.backend.dto;

import java.time.LocalDateTime;

public record NoteResponse(
    Long id,
    String title,
    String content,
    String imageUrl, // We send the Presigned URL, not the S3 Key
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}