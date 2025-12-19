package com.poseidoncapital.service;

import com.poseidoncapital.domain.User;
import com.poseidoncapital.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de gestion des utilisateurs
 *
 * Responsabilités :
 * - Créer/modifier/supprimer des utilisateurs
 * - Synchroniser avec Keycloak
 * - Gérer les erreurs métier
 */
@RequiredArgsConstructor
@Service
public class UserService {

    private final KeycloakAdminService keycloakAdminService;
    private final UserRepository userRepository;

    /**
     * Créer un nouvel utilisateur dans Keycloak ET dans la BDD
     *
     * Workflow :
     * 1. Valider les données (déjà fait par @Valid dans le controller)
     * 2. Créer dans Keycloak (génère le keycloakId)
     * 3. Sauvegarder en BDD avec le keycloakId
     *
     * @param user L'utilisateur à créer (sans keycloakId)
     * L'utilisateur sauvegardé (avec keycloakId et id BDD)
     * @throws RuntimeException Si la création échoue
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
            user.setPassword("KEYCLOAK_AUTH");
            userRepository.save(user);
        } catch (Exception e) {
            if (keycloakId != null) {
                try {
                    keycloakAdminService.deleteUserById(keycloakId);
                } catch (Exception rollbackException) {
                    rollbackException.printStackTrace();
                }
            }
            throw new RuntimeException("Impossible de créer l'utilisateur : " + e.getMessage(), e);
        }
    }

    /**
     * Mettre à jour un utilisateur existant
     * Synchronise avec Keycloak (password et rôle)
     *
     * @param userForm L'utilisateur avec les nouvelles données
     */
    @Transactional
    public void updateUser(User userForm) {
        User user = userRepository.findById(userForm.getId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé : " + userForm.getId()));
        try {
            if (user.getPassword() != null && !user.getPassword().isEmpty() && !user.getPassword().equals("KEYCLOAK_AUTH")) {
                keycloakAdminService.updateUserPassword(user.getKeycloakId(), user.getPassword());
                user.setPassword("KEYCLOAK_AUTH");
            }
            keycloakAdminService.updateUserRole(user.getKeycloakId(), user.getRole());
            user.setFullname(userForm.getFullname());
            user.setRole(userForm.getRole());
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de mettre à jour l'utilisateur : " + e.getMessage(), e);
        }
    }

    /**
     * Supprimer un utilisateur
     *
     * Workflow :
     * 1. Supprimer de la BDD
     * 2. Supprimer de Keycloak
     *
     * @param id L'ID de l'utilisateur dans la BDD
     */
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé : " + id));
        try {
            userRepository.delete(user);
            keycloakAdminService.deleteUserById(user.getKeycloakId());
        } catch (Exception e) {
            throw new RuntimeException("Impossible de supprimer l'utilisateur", e);
        }
    }
}