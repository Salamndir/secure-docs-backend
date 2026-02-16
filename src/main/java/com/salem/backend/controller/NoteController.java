package com.salem.backend.controller;

import com.salem.backend.dto.NoteRequest;
import com.salem.backend.dto.NoteResponse;
import com.salem.backend.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    /**
     * Create a new note.
     * Expects 'multipart/form-data'.
     * Part 1: 'data' -> JSON (title, content)
     * Part 2: 'file' -> Image (Optional)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoteResponse> createNote(
            @Valid @RequestPart("data") NoteRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        NoteResponse response = noteService.createNote(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all notes for the authenticated user.
     */
    @GetMapping
    public ResponseEntity<List<NoteResponse>> getMyNotes() {
        return ResponseEntity.ok(noteService.getUserNotes());
    }



    /**
     * Update an existing note.
     * Expects multipart/form-data (JSON + Optional File).
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable Long id,
            @Valid @RequestPart("data") NoteRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        NoteResponse response = noteService.updateNote(id, request, file);
        return ResponseEntity.ok(response);
    }


   /** 
    * Delete a note by its ID
   */

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteNote(@PathVariable Long id) {


       noteService.deleteNote(id);
    return ResponseEntity.noContent().build();
   }




   
}