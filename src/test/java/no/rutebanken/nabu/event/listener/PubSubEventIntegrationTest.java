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
import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.event.EventService;
import no.rutebanken.nabu.event.listener.dto.JobEventDTO;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;

import static org.mockito.Mockito.timeout;


public class PubSubEventIntegrationTest extends BaseIntegrationTest {


    @Autowired
    private PubSubTemplate pubSubTemplate;


    @MockBean
    private EventService eventService;

    @Captor
    ArgumentCaptor<Event> captor;


    @Test
    public void testConsumeJobEventFromPubSub() {

        JobEventDTO jobEventDTO = new JobEventDTO();
        jobEventDTO.name = "testName";
        jobEventDTO.action = "testAction";
        jobEventDTO.correlationId = "testCorrelationId";
        String testPayload = JobEventDTO.toString(jobEventDTO);

        pubSubTemplate.publish(JobEventListener.JOB_EVENT_QUEUE, testPayload);

        Mockito.verify(eventService, timeout(5000).times(1)).addEvent(captor.capture());
        Event event = captor.getValue();

        Assert.assertEquals("testName", event.getName());
        Assert.assertEquals("testAction", event.getAction());
        Assert.assertEquals("testCorrelationId", event.getCorrelationId());

    }
}
