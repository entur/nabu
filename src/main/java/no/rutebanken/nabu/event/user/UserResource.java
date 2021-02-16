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

package no.rutebanken.nabu.event.user;

import no.rutebanken.nabu.event.user.dto.user.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Fetch user info from organisation registry (Baba)
 */
@Service
public class UserResource {

    @Value("${user.registry.rest.service.url:http://baba/services/organisations/users?full=true}")
    private String restServiceUrl;

    @Autowired
    private WebClient webClient;


    public List<UserDTO> findAll() {

        return webClient.get()
                .uri(restServiceUrl)
                .retrieve()
                .bodyToFlux(UserDTO.class)
                .collectList().block();

    }


}