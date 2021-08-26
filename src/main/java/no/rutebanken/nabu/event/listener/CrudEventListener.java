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

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.event.EventService;
import no.rutebanken.nabu.event.listener.dto.CrudEventDTO;
import no.rutebanken.nabu.event.listener.mapper.EventMapper;
import org.entur.pubsub.base.AbstractEnturGooglePubSubConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Profile("!pubsub-listener-disabled")
public class CrudEventListener extends AbstractEnturGooglePubSubConsumer {

    public static final String CRUD_EVENT_QUEUE = "CrudEventQueue";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventService eventService;

    private EventMapper eventMapper = new EventMapper();

    @Override
    protected String getDestinationName() {
        return CRUD_EVENT_QUEUE;
    }

    @Override
    public void onMessage(byte[] content, Map<String, String> headers) {

        CrudEventDTO dto = CrudEventDTO.fromString(new String(content));
        Event event = eventMapper.toCrudEvent(dto);
        logger.info("Received crud event: {}", event);
        eventService.addEvent(event);
    }


}
