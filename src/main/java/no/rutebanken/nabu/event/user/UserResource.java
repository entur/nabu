package no.rutebanken.nabu.event.user;

import no.rutebanken.nabu.event.user.dto.user.UserDTO;
import no.rutebanken.nabu.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Fetch user info from organisation registry (Baba)
 */
@Service
public class UserResource {

    @Value("${user.registry.rest.service.url:http://baba/services/organisations/users}")
    private String restServiceUrl;

    @Autowired
    private TokenService tokenService;

    private RestTemplate restTemplate = new RestTemplate();

    public List<UserDTO> findAll() {
        ResponseEntity<List<UserDTO>> rateResponse =
                restTemplate.exchange(restServiceUrl,
                        HttpMethod.GET, tokenService.getEntityWithAuthenticationToken(), new ParameterizedTypeReference<List<UserDTO>>() {
                        });
        return rateResponse.getBody();
    }


}