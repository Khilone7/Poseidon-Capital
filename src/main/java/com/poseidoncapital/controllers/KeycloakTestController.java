package com.poseidoncapital.controllers;

import com.poseidoncapital.domain.User;
import com.poseidoncapital.repositories.UserRepository;
import com.poseidoncapital.service.KeycloakAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 🧪 Controller de TEST uniquement
 *
 * Ce controller sert juste à vérifier que notre intégration Keycloak fonctionne.
 * Il sera supprimé plus tard une fois qu'on aura intégré ça dans le vrai workflow.
 */
@Controller
@RequestMapping("/test-keycloak")
public class KeycloakTestController {

    @Autowired
    private KeycloakAdminService keycloakAdminService;

    @Autowired
    private UserRepository userRepository;

    /**
     * 🧪 Endpoint de test : créer un utilisateur dans Keycloak
     *
     * Accessible via : http://localhost:8080/test-keycloak/create-user
     *
     * Ce qu'il fait :
     * 1. Appelle le service Keycloak
     * 2. Crée un utilisateur "testuser1" avec le rôle USER
     * 3. Retourne l'ID Keycloak en cas de succès
     */
    @GetMapping("/create-user")
    @ResponseBody
    public String testCreateUser() {
        try {
            // 📝 Créer un utilisateur de test
            String keycloakId = keycloakAdminService.createUser(
                    "testuser1",           // username
                    "TestPass123!",        // password (conforme aux règles)
                    "USER"                 // role
            );

            // ✅ Succès !
            return "✅ Utilisateur créé avec succès dans Keycloak !<br>" +
                    "Username: testuser1<br>" +
                    "Password: TestPass123!<br>" +
                    "Role: USER<br>" +
                    "Keycloak ID: " + keycloakId + "<br><br>" +
                    "👉 Allez vérifier dans Keycloak Admin Console :<br>" +
                    "<a href='http://localhost:8180/admin/master/console/#/poseidonCapital-realm/users' target='_blank'>" +
                    "http://localhost:8180/admin/.../users</a>";

        } catch (Exception e) {
            // ❌ Erreur
            return "❌ Erreur lors de la création de l'utilisateur :<br>" +
                    e.getMessage() + "<br><br>" +
                    "Stacktrace :<br>" +
                    getStackTrace(e);
        }
    }

    /**
     * Helper pour afficher la stacktrace de manière lisible dans le navigateur
     */
    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("<br>");
        }
        return sb.toString();
    }
    /**
     * 🧪 Test 2 : Créer dans Keycloak ET dans la BDD (COMPLET)
     */
    @GetMapping("/create-user-complete")
    @ResponseBody
    public String testCreateUserComplete() {
        try {
            // 1️⃣ ÉTAPE 1 : Créer dans Keycloak
            System.out.println("📝 Étape 1 : Création dans Keycloak...");
            String keycloakId = keycloakAdminService.createUser(
                    "testuser2",
                    "TestPass456!",
                    "ADMIN"
            );
            System.out.println("✅ Keycloak ID reçu : " + keycloakId);

            // 2️⃣ ÉTAPE 2 : Créer dans la BDD
            System.out.println("📝 Étape 2 : Création dans la BDD...");
            User user = new User();
            user.setUsername("testuser2");
            user.setFullname("Test User Numero 2");
            user.setRole("ADMIN");
            user.setPassword("NOT_USED_KEYCLOAK_MANAGES_AUTH");
            user.setKeycloakId(keycloakId);  // ✨ Le lien entre BDD et Keycloak

            User savedUser = userRepository.save(user);
            System.out.println("✅ User sauvegardé en BDD avec ID : " + savedUser.getId());

            // ✅ Succès complet !
            return "🎉 SUCCÈS COMPLET !<br><br>" +

                    "🔐 <strong>Dans Keycloak :</strong><br>" +
                    "- Keycloak ID : " + keycloakId + "<br>" +
                    "- Username : testuser2<br>" +
                    "- Password : TestPass456!<br>" +
                    "- Role : ADMIN<br><br>" +

                    "💾 <strong>Dans la BDD :</strong><br>" +
                    "- Database ID : " + savedUser.getId() + "<br>" +
                    "- Username : " + savedUser.getUsername() + "<br>" +
                    "- Fullname : " + savedUser.getFullname() + "<br>" +
                    "- Role : " + savedUser.getRole() + "<br>" +
                    "- Keycloak ID : " + savedUser.getKeycloakId() + "<br><br>" +

                    "✅ <strong>Vérifications :</strong><br>" +
                    "1. <a href='http://localhost:8180/admin/master/console/#/poseidonCapital-realm/users' target='_blank'>" +
                    "Voir dans Keycloak Admin</a><br>" +
                    "2. Vérifier en BDD : <code>SELECT * FROM users WHERE username='testuser2';</code><br>" +
                    "3. <a href='http://localhost:8080/login'>Tester la connexion</a> avec testuser2 / TestPass456!";

        } catch (Exception e) {
            e.printStackTrace();  // Pour voir l'erreur dans la console
            return "❌ Erreur lors de la création : <br>" +
                    e.getClass().getSimpleName() + ": " + e.getMessage() + "<br><br>" +
                    "Regardez la console de votre application pour plus de détails.";
        }
    }
}