package com.salem.backend.service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final S3Template s3Template;

    @Value("${app.s3.bucket}")
    private String bucketName;

    /**
     * Uploads a file to S3/MinIO and returns the stored file key.
     * The key structure follows: userId/UUID.extension
     */
    public String uploadFile(MultipartFile file, String userId) {
        
        // 1. Safety check: ensure file is not empty
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 2. Extract the file extension (e.g., ".png" or ".jpg")
        String originalFilename = file.getOriginalFilename();
        String extension = ""; // Default to empty if no extension found

        if (originalFilename != null && originalFilename.contains(".")) {
            int dotIndex = originalFilename.lastIndexOf(".");
            extension = originalFilename.substring(dotIndex);
        }

        // 3. Generate a unique key to prevent collisions
        String key = userId + "/" + UUID.randomUUID().toString() + extension;

        try {
            log.info("Uploading file to S3 bucket: {}, key: {}", bucketName, key);
            
            // 4. Upload stream to S3
            InputStream inputStream = file.getInputStream();
            s3Template.upload(bucketName, key, inputStream);
            
            return key;
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            // Throwing a RuntimeException that will be caught by GlobalExceptionHandler
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    /**
     * Generates a pre-signed URL to access private files securely.
     * The URL is valid for a specific duration (e.g., 1 hour).
     */
    public String getFileUrl(String key) {
        if (key == null || key.isBlank()) return null;
        
        // Generate a URL valid for 1 hour
        String url = s3Template.createSignedGetURL(bucketName, key, Duration.ofHours(1)).toString();
        
        log.debug("Generated signed URL for key {}", key); // Changed to debug to reduce noise
        return url;
    }



    /**
     * Deletes a file from S3 bucket using its key.
     */
    public void deleteFile(String key) {
        if (key == null || key.isBlank()) {
            log.warn("Attempted to delete file with empty key, ignoring.");
            return;
        }
        
        try {
            log.info("Deleting file from S3: {}", key);
            s3Template.deleteObject(bucketName, key);
        } catch (Exception e) {
            
            log.error("Failed to delete file from S3: " + key, e);
        }
    }




}