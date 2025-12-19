package com.poseidoncapital.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Service
public class KeycloakAdminService {

    // 📌 Injection des propriétés depuis application.properties
    @Value("${keycloak.admin.server-url}")
    private String serverUrl;

    @Value("${keycloak.admin.realm}")
    private String adminRealm;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Value("${keycloak.admin.client-id}")
    private String adminClientId;

    @Value("${keycloak.admin.target-realm}")
    private String targetRealm;

    /**
     * 🔑 Méthode privée pour obtenir une connexion admin à Keycloak
     *
     * Cette méthode :
     * 1. Se connecte au realm "master" avec le compte admin
     * 2. Obtient un token d'administration
     * 3. Retourne un objet Keycloak qu'on peut utiliser pour faire des actions admin
     */
    private Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .username(adminUsername)
                .password(adminPassword)
                .clientId(adminClientId)
                .build();
    }

    /**
     * Créer un utilisateur dans Keycloak
     *
     * @param username Le nom d'utilisateur
     * @param password Le mot de passe (en clair, Keycloak va le hasher)
     * @param role Le rôle à assigner (ex: "ADMIN" ou "USER")
     * @return L'ID Keycloak du nouvel utilisateur
     */
    public String createUser(String username, String password, String role) {
        //Obtenir une connexion admin
        Keycloak keycloak = getKeycloakInstance();

        try {
            // Accéder au realm cible
            RealmResource realmResource = keycloak.realm(targetRealm);
            UsersResource usersResource = realmResource.users();

            // Préparer l'objet utilisateur
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEnabled(true);          // Utilisateur actif dès la création
            user.setEmailVerified(true);

            // Créer l'utilisateur dans Keycloak
            Response response = usersResource.create(user);

            // Vérifier que la création a réussi
            if (response.getStatus() != 201) {
                throw new RuntimeException("Erreur création utilisateur: " + response.getStatusInfo());
            }

            // Récupérer l'ID du nouvel utilisateur
            // L'API retourne l'URL : /admin/realms/poseidonCapital-realm/users/a3b2c1...
            // On extrait juste l'ID à la fin
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            // Définir le mot de passe
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);  // false = pas besoin de changer au premier login

            usersResource.get(userId).resetPassword(credential);

            // Assigner le rôle
            RoleRepresentation roleRepresentation = realmResource.roles()
                    .get(role)  // Récupère le rôle "ADMIN" ou "USER"
                    .toRepresentation();

            usersResource.get(userId).roles().realmLevel()
                    .add(Collections.singletonList(roleRepresentation));

            // Nettoyer et retourner l'ID
            response.close();
            keycloak.close();

            return userId;

        } catch (Exception e) {
            keycloak.close();
            throw new RuntimeException("Erreur lors de la création de l'utilisateur dans Keycloak", e);
        }
    }

    /**
     * Supprimer un utilisateur de Keycloak par son ID
     *
     * @param keycloakUserId L'UUID de l'utilisateur dans Keycloak
     */
    public void deleteUserById(String keycloakUserId) {
        Keycloak keycloak = getKeycloakInstance();
        try {
            RealmResource realmResource = keycloak.realm(targetRealm);
            UsersResource usersResource = realmResource.users();
            usersResource.delete(keycloakUserId);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de supprimer l'utilisateur de Keycloak", e);
        } finally {
            keycloak.close();
        }
    }

    /**
     * Mettre à jour le mot de passe d'un utilisateur dans Keycloak
     *
     * @param keycloakUserId L'UUID de l'utilisateur dans Keycloak
     * @param newPassword Le nouveau mot de passe (en clair)
     */
    public void updateUserPassword(String keycloakUserId, String newPassword) {
        Keycloak keycloak = getKeycloakInstance();

        try {
            RealmResource realmResource = keycloak.realm(targetRealm);
            UsersResource usersResource = realmResource.users();

            // Préparer le nouveau credential
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);

            // Mettre à jour le password
            usersResource.get(keycloakUserId).resetPassword(credential);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de mettre à jour le password dans Keycloak", e);
        } finally {
            keycloak.close();
        }
    }

    /**
     * Mettre à jour le rôle d'un utilisateur dans Keycloak
     *
     * @param keycloakUserId L'UUID de l'utilisateur dans Keycloak
     * @param newRole Le nouveau rôle ("ADMIN" ou "USER")
     */
    public void updateUserRole(String keycloakUserId, String newRole) {
        Keycloak keycloak = getKeycloakInstance();

        try {
            RealmResource realmResource = keycloak.realm(targetRealm);
            UsersResource usersResource = realmResource.users();

            // 1. Récupérer les rôles actuels
            List<RoleRepresentation> currentRoles = usersResource.get(keycloakUserId)
                    .roles()
                    .realmLevel()
                    .listAll();

            // 2. Supprimer tous les rôles actuels (ADMIN et USER)
            if (!currentRoles.isEmpty()) {
                usersResource.get(keycloakUserId)
                        .roles()
                        .realmLevel()
                        .remove(currentRoles);
            }

            // 3. Ajouter le nouveau rôle
            RoleRepresentation roleRepresentation = realmResource.roles()
                    .get(newRole)
                    .toRepresentation();

            usersResource.get(keycloakUserId)
                    .roles()
                    .realmLevel()
                    .add(Collections.singletonList(roleRepresentation));

        } catch (Exception e) {
            throw new RuntimeException("Impossible de mettre à jour le rôle dans Keycloak", e);
        } finally {
            keycloak.close();
        }
    }
}