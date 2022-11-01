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
import no.rutebanken.nabu.exceptions.NabuException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

class CrudeEventListenerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CrudeEventProcessor crudeEventProcessor;


    @Test
    // Use propagation = never so that the transaction boundaries are within jobEventProcessor.processMessage()
    @Transactional(propagation = Propagation.NEVER)
    void saveInvalidCrudEvent() {
        CrudEventDTO invalidCrudEvent = createEvent("X".repeat(500));
        String json = toJson(invalidCrudEvent);
        Assertions.assertThrows(NabuException.class, () -> crudeEventProcessor.processMessage(json));
    }


    protected CrudEventDTO createEvent(String action) {
        CrudEventDTO crudEvent = new CrudEventDTO();
        crudEvent.eventTime = Instant.now();
        crudEvent.action = action;
        return crudEvent;
    }


}
