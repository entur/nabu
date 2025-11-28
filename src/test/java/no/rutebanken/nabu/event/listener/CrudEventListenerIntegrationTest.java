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

package no.rutebanken.nabu.event.listener;

import no.rutebanken.nabu.BaseIntegrationTest;
import no.rutebanken.nabu.event.listener.dto.CrudEventDTO;
import no.rutebanken.nabu.event.user.UserRepository;
import no.rutebanken.nabu.repository.EventRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.ArrayList;

import static org.mockito.Mockito.when;

class CrudEventListenerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CrudeEventProcessor crudeEventProcessor;

    @MockitoBean
    private UserRepository userRepositoryMock;

    @BeforeEach
    void setUp() {
        when(userRepositoryMock.findAll()).thenReturn(new ArrayList<>());
    }

    @Test
    void saveValidCrudEvent() {
        CrudEventDTO validCrudEvent = createEvent(Instant.now(), "UPDATE", "X", 0L);
        String json = toJson(validCrudEvent);
        crudeEventProcessor.processMessage(json);
        Assertions.assertDoesNotThrow(() -> eventRepository.flush());
        Assertions.assertEquals(1, eventRepository.findAll().size());
    }

    @Test
    void ignoreInvalidCrudEvent() {
        CrudEventDTO invalidCrudEvent = createEvent(Instant.now(), "X".repeat(500), "X", 0L);
        String json = toJson(invalidCrudEvent);
        crudeEventProcessor.processMessage(json);
        Assertions.assertDoesNotThrow(() -> eventRepository.flush());
        Assertions.assertEquals(0, eventRepository.findAll().size());
    }


    protected CrudEventDTO createEvent(Instant time, String action, String entityType, Long version) {
        CrudEventDTO crudEvent = new CrudEventDTO();
        crudEvent.action = action;
        crudEvent.eventTime = time;
        crudEvent.entityType = entityType;
        crudEvent.version = version;
        return crudEvent;
    }


}
