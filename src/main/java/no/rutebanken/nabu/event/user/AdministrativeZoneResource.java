package no.rutebanken.nabu.event.user;

import no.rutebanken.nabu.event.user.dto.organisation.AdministrativeZoneDTO;
import no.rutebanken.nabu.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AdministrativeZoneResource {

    @Value("${administrative.zone.registry.rest.service.url:http://baba/services/organisations/administrative_zones}")
    private String restServiceUrl;

    @Autowired
    private TokenService tokenService;

    private RestTemplate restTemplate = new RestTemplate();

    public AdministrativeZoneDTO getAdministrativeZone(String id) {
        ResponseEntity<AdministrativeZoneDTO> rateResponse =
                restTemplate.exchange(restServiceUrl + id,
                        HttpMethod.GET, tokenService.getEntityWithAuthenticationToken(), new ParameterizedTypeReference<AdministrativeZoneDTO>() {
                        });
        return rateResponse.getBody();
    }

}
