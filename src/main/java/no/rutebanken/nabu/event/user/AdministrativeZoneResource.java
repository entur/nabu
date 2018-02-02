/*
 *
 *  * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 *  * the European Commission - subsequent versions of the EUPL (the "Licence");
 *  * You may not use this work except in compliance with the Licence.
 *  * You may obtain a copy of the Licence at:
 *  *
 *  *   https://joinup.ec.europa.eu/software/page/eupl
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the Licence is distributed on an "AS IS" basis,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the Licence for the specific language governing permissions and
 *  * limitations under the Licence.
 *
 */

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

    @Value("${administrative.zone.registry.rest.service.url:http://baba/services/organisations/administrative_zones/}")
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
