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

package no.rutebanken.nabu.jms;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.event.EventService;
import no.rutebanken.nabu.jms.dto.JobEventDTO;
import no.rutebanken.nabu.jms.mapper.EventMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class JobEventListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventService eventService;

    private EventMapper eventMapper = new EventMapper();

    @JmsListener(destination = "JobEventQueue")
    public void processMessage(String content) {
        JobEventDTO dto = JobEventDTO.fromString(content);

        Event event = eventMapper.toJobEvent(dto);
        logger.info("Received job event: " + event);
        eventService.addEvent(event);
    }

}
