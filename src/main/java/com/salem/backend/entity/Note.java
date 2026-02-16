package com.salem.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT") // Explicitly defines TEXT type for long content in DB
    private String content;

    @Column(name = "image_key")
    private String imageKey;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // Automatically updates the timestamp upon modification
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship: Many notes belong to one user.
    // FetchType.LAZY: Performance optimization! User data is loaded only when explicitly requested.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // The foreign key column in the database
    private User user;
}