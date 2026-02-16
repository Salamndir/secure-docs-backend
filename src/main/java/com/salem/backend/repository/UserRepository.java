package com.salem.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.salem.backend.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Select * from users where keycloak_id = ?
    Optional<User> findByKeycloakId(String keycloakId);
    
    // للتحقق هل المستخدم موجود أم لا (أسرع من جلب البيانات كاملة)
    boolean existsByKeycloakId(String keycloakId);
}