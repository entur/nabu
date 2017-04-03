package no.rutebanken.nabu.organisation.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyCloakClientConfiguration {

	@Value("${iam.keycloak.admin.path}")
	private String adminPath;

	@Value("${iam.keycloak.admin.realm:master}")
	private String masterRealm;

	@Value("${iam.keycloak.user.realm:rutebanken}")
	private String userRealm;

	@Value("${iam.keycloak.admin.username:nabu}")
	private String username;

	@Value("${iam.keycloak.admin.password}")
	private String password;

	@Value("${iam.keycloak.admin.client:nabu-bot}")
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
