/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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
