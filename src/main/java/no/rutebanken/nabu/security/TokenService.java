package no.rutebanken.nabu.security;

import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Autowired
    private Keycloak keycloakClient;

    public String getToken() {
        return keycloakClient.tokenManager().getAccessTokenString();
    }


    public HttpEntity<String> getEntityWithAuthenticationToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getToken());
        return new HttpEntity<>(headers);
    }
}
