package com.poseidoncapital.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "keycloak.admin")
@Component
public class KeycloakPropertiesConfig {

    private String serverUrl;
    private String realm;
    private String username;
    private String password;
    private String clientId;
    private String targetRealm;
}
