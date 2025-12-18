package com.poseidoncapital.service;

import com.poseidoncapital.domain.User;
import com.poseidoncapital.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
@Service
public class UserService {

    @Autowired
    private KeycloakAdminService keycloakAdminService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Créer un nouvel utilisateur dans Keycloak ET dans la BDD
     *
     * Workflow :
     * 1. Valider les données (déjà fait par @Valid dans le controller)
     * 2. Créer dans Keycloak (génère le keycloakId)
     * 3. Sauvegarder en BDD avec le keycloakId
     *
     * @param user L'utilisateur à créer (sans keycloakId)
     * @return L'utilisateur sauvegardé (avec keycloakId et id BDD)
     * @throws RuntimeException Si la création échoue
     */
    @Transactional
    public User createUser(User user) {
        try {
            // Créer dans Keycloak
            String keycloakId = keycloakAdminService.createUser(
                    user.getUsername(),
                    user.getPassword(),  // Password en clair, Keycloak va le hasher
                    user.getRole()
            );

            // Préparer l'entité pour la BDD
            user.setKeycloakId(keycloakId);

            // ⚠️ Le password n'est plus nécessaire en BDD (Keycloak gère l'auth)
            // Mais on garde le champ pour compatibilité
            user.setPassword("KEYCLOAK_AUTH");

            // Sauvegarder en BDD
            User savedUser = userRepository.save(user);

            return savedUser;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création de l'utilisateur : " + e.getMessage());

            // TODO Phase 3 : Rollback Keycloak si BDD échoue
            // Pour l'instant, on laisse comme ça

            throw new RuntimeException("Impossible de créer l'utilisateur : " + e.getMessage(), e);
        }
    }

    /**
     * Mettre à jour un utilisateur existant
     * Synchronise avec Keycloak (password et rôle)
     *
     * @param userForm L'utilisateur avec les nouvelles données
     * @return L'utilisateur mis à jour
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
            // Supprimer de la BDD
            userRepository.delete(user);
            System.out.println("✅ Utilisateur supprimé de la BDD : " + user.getUsername());

            // Supprimer de Keycloak (si l'utilisateur a un keycloakId)
            if (user.getKeycloakId() != null && !user.getKeycloakId().isEmpty()) {
                keycloakAdminService.deleteUserById(user.getKeycloakId());
                System.out.println("✅ Utilisateur supprimé de Keycloak : " + user.getKeycloakId());
            }

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression : " + e.getMessage());
            throw new RuntimeException("Impossible de supprimer l'utilisateur", e);
        }
    }
}