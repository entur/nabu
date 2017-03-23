package no.rutebanken.nabu.organisation.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyCloakClientConfiguration {

	@Value("${keycloak.admin.path:http://localhost:8080/auth}")
	private String adminPath;

	@Value("${keycloak.admin.realm:master}")
	private String masterRealm;

	@Value("${keycloak.admin.realm:Rutebanken}")
	private String userRealm;

	@Value("${keycloak.admin.username:user}")
	private String username;

	@Value("${keycloak.admin.password:pass}")
	private String password;
	@Value("${keycloak.admin.client:Nabu}")
	private String clientId;

	@Bean
	public RealmResource keycloakAdminClient() {
		return KeycloakBuilder.builder()
				       .serverUrl(adminPath)
				       .realm(masterRealm)
				       .username(username)
				       .password(password)
				       .clientId(clientId)
				       .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
				       .build().realm(userRealm);

	}
}
