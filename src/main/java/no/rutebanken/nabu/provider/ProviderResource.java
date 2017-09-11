package no.rutebanken.nabu.provider;


import no.rutebanken.nabu.provider.model.Provider;
import no.rutebanken.nabu.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;

@Component
public class ProviderResource {

    @Value("${provider.registry.rest.service.url:http://baba/services/providers/}")
    private String restServiceUrl;


    @Autowired
    private TokenService tokenService;


    public Collection<Provider> getProviders() {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<Provider>> rateResponse =
                restTemplate.exchange(restServiceUrl,
                        HttpMethod.GET, tokenService.getEntityWithAuthenticationToken(), new ParameterizedTypeReference<List<Provider>>() {
                        });
        return rateResponse.getBody();
    }

}
