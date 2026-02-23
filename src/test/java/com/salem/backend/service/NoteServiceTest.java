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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

// 1. We tell JUnit to run this test with Mockito's environment (No Spring Boot, No Database)
@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    // 2. We mock ALL external dependencies that NoteService relies on.
    // They act like empty shells (Black holes) that do nothing unless we tell them to.
    @Mock
    private NoteRepository noteRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private NoteMapper noteMapper;
    @Mock
    private SecurityUtils securityUtils;

    // 3. We inject all the mocks above into our real NoteService instance.
    @InjectMocks
    private NoteService underTest;

    // 4. We prepare our "Spy Camera" to capture the Note entity right before it enters the database.
    @Captor
    private ArgumentCaptor<Note> noteArgumentCaptor;

    // ========================================================================
    // TEST 1: The Happy Path with ArgumentCaptor
    // ========================================================================
    @Test
    void createNote_WithValidRequestAndNoFile_ShouldSaveNoteSuccessfully() {


        // -------------------- given (Arrange) --------------------------
        
        NoteRequest request = new NoteRequest("My Valid Title", "My Valid Content");
        
        User fakeUser = new User();
        fakeUser.setId(100L); 
        
        given(securityUtils.getCurrentUser()).willReturn(fakeUser);
        
        Note dummySavedNote = new Note();
        dummySavedNote.setId(1L);
        given(noteRepository.save(any(Note.class))).willReturn(dummySavedNote);
        
        NoteResponse expectedResponse = new NoteResponse(1L, "My Valid Title", "My Valid Content", null, null, null);
        given(noteMapper.toResponse(dummySavedNote, null)).willReturn(expectedResponse);

        // ------------------------------ when (Act) ------------------------

        NoteResponse actualResponse = underTest.createNote(request, null);


        // ----------------------------- then (Assert) ----------------------


        assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(noteRepository).save(noteArgumentCaptor.capture());

        Note capturedNote = noteArgumentCaptor.getValue();

        assertThat(capturedNote.getTitle()).isEqualTo(request.title());
        assertThat(capturedNote.getContent()).isEqualTo(request.content());
        
        assertThat(capturedNote.getUser()).isEqualTo(fakeUser);
        
        assertThat(capturedNote.getImageKey()).isNull();
    }

    // ========================================================================
    // TEST 2: The Security/Exception Path (Testing Business Rules)
    // ========================================================================
    @Test
    void updateNote_WhenHackerTriesToUpdate_ShouldThrowException() {
        
        // ------------ (Arrange) -------------
        
        User hacker = new User();
        hacker.setId(999L); 
        
        User realOwner = new User();
        realOwner.setId(100L); 
        
        Note existingNote = new Note();
        existingNote.setId(1L);
        existingNote.setUser(realOwner);
        
        //  ---------------(given) ------------
        given(securityUtils.getCurrentUser()).willReturn(hacker);
        
        given(noteRepository.findById(1L)).willReturn(Optional.of(existingNote));


        // --------- (Act & Assert) ------------
        
        assertThatThrownBy(() -> {
            underTest.updateNote(1L, new NoteRequest("Hack", "Hack"), null);
        }).isInstanceOf(BusinessException.class)
          .hasMessageContaining(enErrorCode.NOTE_OWNERSHIP_ERROR.getMessageKey());


        
        verify(noteRepository, never()).save(any());
    }



    // ========================================================================
    // TEST 3: Get User Notes (Empty List Scenario)
    // ========================================================================

    @Test
    void getUserNotes_WhenUserHasNoNotes_ShouldReturnEmptyList(){

        // ------------ (Arrange & Given) -------------

        User user = new User();
        user.setId(5l);
        given(securityUtils.getCurrentUser()).willReturn(user);

        given(noteRepository.findByUserId(5l)).willReturn( new ArrayList<>());


        // --------- (Act) ------------

        List<NoteResponse> result = underTest.getUserNotes();

        // --------- (Assert & Verify) ------------

        assertThat(result).isEmpty();

        verify(noteMapper, never()).toResponse(any(), any());

    }


    // ========================================================================
    // TEST 4: Delete Note (Happy Path) 
    // ========================================================================


    @Test
    void deleteNote_WhenUserIsOwner_ShouldDeleteSuccessfully(){

        // ------------ (Arrange & Given) -------------

        User owner = new User();
        owner.setId(5L);

        Note existingNote = new Note();
        existingNote.setId(10L);
        existingNote.setUser(owner);


        given(securityUtils.getCurrentUser()).willReturn(owner);

        given(noteRepository.findById(existingNote.getId())).willReturn(Optional.of(existingNote));


        // --------- (Act) ------------

        underTest.deleteNote(existingNote.getId());

        // --------- (Assert & Verify) ------------


        verify(noteRepository).delete(existingNote);

        verify(fileStorageService, never()).deleteFile(any());



        



    }



    



}