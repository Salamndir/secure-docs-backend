package com.salem.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.salem.backend.entity.Note;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Select * from notes where user_id = ?
    // لاحظ: نستخدم userId (الداخلي) للأداء العالي
    List<Note> findByUserId(Long userId);
}