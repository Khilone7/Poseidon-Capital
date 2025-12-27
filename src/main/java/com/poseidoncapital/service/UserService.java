package com.poseidoncapital.service;

import com.poseidoncapital.domain.User;
import com.poseidoncapital.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User management service
 * Handles user lifecycle operations and Keycloak synchronization
 */
@RequiredArgsConstructor
@Service
public class UserService {

    private final KeycloakAdminService keycloakAdminService;
    private final UserRepository userRepository;

    /**
     * Creates a new user in both Keycloak and the database
     * The operation is atomic: if database save fails, Keycloak user is rolled back
     *
     * @param user User to create (keycloakId will be generated)
     * @throws IllegalArgumentException if username already exists
     * @throws RuntimeException if creation fails
     */
    @Transactional
    public void createUser(User user) {
        String keycloakId = null;
        try {
            keycloakId = keycloakAdminService.createUser(
                    user.getUsername(),
                    user.getPassword(),
                    user.getRole()
            );
            user.setKeycloakId(keycloakId);
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            if (keycloakId != null) {
                rollbackKeycloakUser(keycloakId);
            }
            throw new RuntimeException("Unable to create user : " + e.getMessage(), e);
        }
    }

    private void rollbackKeycloakUser(String keycloakId) {
        try {
            keycloakAdminService.deleteUserById(keycloakId);
        } catch (Exception rollbackException) {
            System.err.println("Failed to rollback Keycloak user: " + keycloakId);
        }
    }

    /**
     * Updates an existing user's password, role, and profile information
     * Changes are synchronized to Keycloak
     *
     * @param userForm User object containing updated values
     * @throws IllegalArgumentException if user not found or password invalid
     * @throws RuntimeException if update fails
     */
    @Transactional
    public void updateUser(User userForm) {
        User user = userRepository.findById(userForm.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found : " + userForm.getId()));
        try {
            keycloakAdminService.updateUserPassword(user.getKeycloakId(), userForm.getPassword());
            keycloakAdminService.updateUserRole(user.getKeycloakId(), userForm.getRole());
            user.setFullname(userForm.getFullname());
            user.setRole(userForm.getRole());
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unable to update user : " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a user from both database and Keycloak
     * Database deletion occurs first; if Keycloak deletion fails, transaction is rolled back
     *
     * @param id Database ID of the user to delete
     * @throws IllegalArgumentException if user not found
     * @throws RuntimeException if deletion fails
     */
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found : " + id));
        try {
            userRepository.delete(user);
            keycloakAdminService.deleteUserById(user.getKeycloakId());
        } catch (Exception e) {
            throw new RuntimeException("Unable to delete user", e);
        }
    }
}