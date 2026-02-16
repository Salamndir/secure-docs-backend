package com.salem.backend.util;

import com.salem.backend.entity.User;
import com.salem.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityUtils {

    private final UserRepository userRepository;

    /**
     * Retrieves the currently authenticated user from the database.
     * If the user exists in the Token (Keycloak) but not in DB, it creates them (JIT Provisioning).
     */
    public User getCurrentUser() {
        Jwt jwt = getPrincipal();
        String keycloakId = jwt.getSubject();

        return userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> syncUserFromToken(jwt));
    }

    /**
     * Helper to get the JWT Principal from Security Context.
     */
    private Jwt getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            throw new IllegalStateException("No authenticated user found / Invalid Token");
        }
        return (Jwt) authentication.getPrincipal();
    }

    /**
     * Creates a new user record in local DB based on Keycloak Token claims.
     */
    private User syncUserFromToken(Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");

        log.info("Synchronizing new user from Keycloak: {}", email);

        User newUser = User.builder()
                .keycloakId(keycloakId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        return userRepository.save(newUser);
    }
}