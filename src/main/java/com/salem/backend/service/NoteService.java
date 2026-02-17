package com.salem.backend.service;

import com.salem.backend.dto.NoteRequest;
import com.salem.backend.dto.NoteResponse;
import com.salem.backend.entity.Note;
import com.salem.backend.entity.User;
import com.salem.backend.exception.BusinessException;
import com.salem.backend.exception.enErrorCode;
import com.salem.backend.mapper.NoteMapper;
import com.salem.backend.repository.NoteRepository;
import com.salem.backend.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private final NoteRepository noteRepository;
    private final FileStorageService fileStorageService;
    private final NoteMapper noteMapper;
    private final SecurityUtils securityUtils;

    /**
     * Creates a new note for the authenticated user.
     * Handles image upload if a file is provided.
     */
    @Transactional
    public NoteResponse createNote(NoteRequest request, MultipartFile file) {
        
        // 1. Get the currently authenticated user
        User user = securityUtils.getCurrentUser();

        // 2. Upload Image if exists
        String imageKey = null;
        if (file != null && !file.isEmpty()) {
            imageKey = fileStorageService.uploadFile(file, user.getKeycloakId());
        }

        // 3. Build the Note entity
        Note note = Note.builder()
                .title(request.title())
                .content(request.content())
                .imageKey(imageKey)
                .user(user)
                .build();

        // 4. Save to database
        Note savedNote = noteRepository.save(note);
        log.info("Note created successfully with ID: {}", savedNote.getId());

        // 5. Map to Response DTO
        return mapToResponse(savedNote);
    }

    /**
     * Retrieves all notes belonging to the current user.
     */
    @Transactional(readOnly = true)
    public List<NoteResponse> getUserNotes() {

        User user = securityUtils.getCurrentUser();

        List<Note> notes = noteRepository.findByUserId(user.getId());

        if (notes.isEmpty()) {
            // throw new BusinessException(enErrorCode.NOTE_NOT_FOUND); // Optional: Decide if you want to throw an exception or return an empty list when no notes are found
            log.info("No notes found for user with ID: {}", user.getId());

            return new ArrayList<>(); // Return empty list if no notes are found
        }


        
        List<NoteResponse> responseList = new ArrayList<>();

    
        for (Note note : notes) {
            
            NoteResponse response = mapToResponse(note);
            responseList.add(response);
        }

        return responseList;
    }

    /**
     * Updates an existing note.
     * Validates ownership before updating.
     */
    @Transactional
    public NoteResponse updateNote(Long noteId, NoteRequest request, MultipartFile file) {
        User currentUser = securityUtils.getCurrentUser();

        // 1. Find the note or throw BusinessException 
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new BusinessException(enErrorCode.NOTE_NOT_FOUND));

        // 2. Security Check: Ownership validation
        if (!note.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(enErrorCode.NOTE_OWNERSHIP_ERROR);
        }

        // 3. Update fields
        note.setTitle(request.title());
        note.setContent(request.content());

        // 4. Update Image (Only if a new file is uploaded)
        if (file != null && !file.isEmpty()) {
            String newImageKey = fileStorageService.uploadFile(file, currentUser.getKeycloakId());
            note.setImageKey(newImageKey);
            // Future improvement: Delete old image from S3 to save space
        }

        // 5. Save changes
        Note updatedNote = noteRepository.save(note);
        log.info("Note with ID: {} updated successfully", noteId);
        
        return mapToResponse(updatedNote);
    }

    // Helper method to convert Entity to DTO and generate S3 URL
    private NoteResponse mapToResponse(Note note) {
        String imageUrl = null;
        if (note.getImageKey() != null) {
            imageUrl = fileStorageService.getFileUrl(note.getImageKey());
        }
        return noteMapper.toResponse(note, imageUrl);
    }



    // Delete a note by its ID
    @Transactional
    public void deleteNote(Long noteId){

    User currentUser = securityUtils.getCurrentUser();

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new BusinessException(enErrorCode.NOTE_NOT_FOUND));
        // Security Check: Ownership validation
        if (!note.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(enErrorCode.NOTE_OWNERSHIP_ERROR);
        }
        
        if (note.getImageKey() != null) {
            fileStorageService.deleteFile(note.getImageKey());
        }

        noteRepository.delete(note);
        log.info("Note with ID: {} deleted successfully", noteId);
    }




}