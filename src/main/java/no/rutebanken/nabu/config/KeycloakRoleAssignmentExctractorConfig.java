package no.rutebanken.nabu.config;

import org.rutebanken.helper.organisation.KeycloakRoleAssignmentExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakRoleAssignmentExctractorConfig {

    @Bean
    public KeycloakRoleAssignmentExtractor getKeycloakRoleAssignmentExtractor() {
        return new KeycloakRoleAssignmentExtractor();
    }

}
