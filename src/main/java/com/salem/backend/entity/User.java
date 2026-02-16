package com.salem.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Matches BIGSERIAL in PostgreSQL
    private Long id;

    @Column(name = "keycloak_id", nullable = false, unique = true)
    private String keycloakId;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @CreationTimestamp // Hibernate automatically populates the timestamp on persist
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relationship: One user has many notes.
    // 'mappedBy' indicates that the 'Note' entity owns the relationship (holds the foreign key).
    // CascadeType.ALL: If the user is deleted, all their notes are deleted.
    // orphanRemoval = true: If a note is removed from this list, it is deleted from the database.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // Prevents Lombok from setting this list to null during the build pattern
    private List<Note> notes = new ArrayList<>();
}