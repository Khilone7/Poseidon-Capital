package com.poseidoncapital.service;

import com.poseidoncapital.configuration.KeycloakPropertiesConfig;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakAdminService {

    private final KeycloakPropertiesConfig keycloakProperties;

    private final String targetRealm = keycloakProperties.getTargetRealm();

    /**
     * Private method to obtain an admin connection to Keycloak
     * <p>
     * This method:
     * 1. Connects to the "master" realm with the admin account
     * 2. Obtains an administration token
     * 3. Returns a Keycloak object that can be used to perform admin actions
     */
    private Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getServerUrl())
                .realm(keycloakProperties.getRealm())
                .username(keycloakProperties.getUsername())
                .password(keycloakProperties.getPassword())
                .clientId(keycloakProperties.getClientId())
                .build();
    }

    /**
     * Create a user in Keycloak
     *
     * @param username The username
     * @param password The password (plain text, Keycloak will hash it)
     * @param role     The role to assign (e.g., "ADMIN" or "USER")
     * @return The Keycloak ID of the new user
     */
    public String createUser(String username, String password, String role) {
        try (Keycloak keycloak = getKeycloakInstance()) {
            RealmResource realmResource = keycloak.realm(targetRealm);
            UsersResource usersResource = realmResource.users();

            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEnabled(true);// User active from creation
            user.setEmailVerified(true);

            String userId;
            try (Response response = usersResource.create(user)) {
                if (response.getStatusInfo().toEnum() == Response.Status.CONFLICT) {
                    throw new IllegalArgumentException("Ce nom d'utilisateur est déjà utilisé");
                }
                if (response.getStatusInfo().toEnum() != Response.Status.CREATED) {
                    throw new RuntimeException("Erreur création utilisateur: " + response.getStatusInfo());
                }

                // Retrieve the new user's ID
                String locationPath = response.getLocation().getPath();
                userId = locationPath.substring(locationPath.lastIndexOf('/') + 1);
            }

            // Set password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);
            usersResource.get(userId).resetPassword(credential);

            RoleRepresentation roleRepresentation = realmResource.roles()
                    .get(role)
                    .toRepresentation();

            usersResource.get(userId).roles().realmLevel()
                    .add(Collections.singletonList(roleRepresentation));

            return userId;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error creating user in Keycloak", e);
        }
    }

    /**
     * Delete a user from Keycloak by their ID
     *
     * @param keycloakUserId The user's UUID in Keycloak
     */
    public void deleteUserById(String keycloakUserId) {
        try (Keycloak keycloak = getKeycloakInstance()) {
            keycloak.realm(targetRealm)
                    .users()
                    .get(keycloakUserId)
                    .remove();
        } catch (Exception e) {
            throw new RuntimeException("Unable to delete user from Keycloak", e);
        }
    }

    /**
     * Update a user's password in Keycloak
     *
     * @param keycloakUserId The user's UUID in Keycloak
     * @param newPassword    The new password (plain text)
     */
    public void updateUserPassword(String keycloakUserId, String newPassword) {
        try (Keycloak keycloak = getKeycloakInstance()) {
            UserResource userResource = keycloak.realm(targetRealm)
                    .users()
                    .get(keycloakUserId);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);

            userResource.resetPassword(credential);
        } catch (BadRequestException e) {
            throw new IllegalArgumentException("Password must contain at least 8 characters, one uppercase letter, one digit and one special character");

        } catch (Exception e) {
            throw new RuntimeException("Unable to update password in Keycloak", e);
        }
    }

    /**
     * Update a user's role in Keycloak
     *
     * @param keycloakUserId The user's UUID in Keycloak
     * @param newRole        The new role ("ADMIN" or "USER")
     */
    public void updateUserRole(String keycloakUserId, String newRole) {
        try (Keycloak keycloak = getKeycloakInstance()) {
            UserResource userResource = keycloak.realm(targetRealm)
                    .users()
                    .get(keycloakUserId);

            List<RoleRepresentation> currentRoles = userResource
                    .roles()
                    .realmLevel()
                    .listAll();

            if (!currentRoles.isEmpty()) {
                userResource.roles().realmLevel().remove(currentRoles);
            }

            RoleRepresentation roleRepresentation = keycloak.realm(targetRealm)
                    .roles()
                    .get(newRole)
                    .toRepresentation();

            userResource.roles()
                    .realmLevel()
                    .add(Collections.singletonList(roleRepresentation));

        } catch (Exception e) {
            throw new RuntimeException("Unable to update role in Keycloak", e);
        }
    }
}