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

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import no.rutebanken.nabu.BaseIntegrationTest;
import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.event.EventService;
import no.rutebanken.nabu.event.listener.dto.JobEventDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.timeout;


class PubSubEventIntegrationTest extends BaseIntegrationTest {


    @Autowired
    private PubSubTemplate pubSubTemplate;


    @MockBean
    private EventService eventService;

    @Captor
    ArgumentCaptor<Event> captor;


    @Test
   void testConsumeJobEventFromPubSub() throws ExecutionException, InterruptedException {

        JobEventDTO jobEventDTO = new JobEventDTO();
        jobEventDTO.setDomain("testDomain");
        jobEventDTO.setName("testName");
        jobEventDTO.setAction("testAction");
        jobEventDTO.setCorrelationId("testCorrelationId");
        jobEventDTO.setState(JobState.OK);
        jobEventDTO.setEventTime(Instant.now());
        String testPayload = JobEventDTO.toString(jobEventDTO);

        CompletableFuture<String> listenableFuture = pubSubTemplate.publish(JobEventListener.JOB_EVENT_QUEUE, testPayload);
        listenableFuture.get();

        Mockito.verify(eventService, timeout(10000).times(1)).addEvent(captor.capture());
        Event event = captor.getValue();
        Assert.isInstanceOf(JobEvent.class, event);
        JobEvent jobEvent = (JobEvent) event;

        Assertions.assertEquals("testDomain", jobEvent.getDomain());
        Assertions.assertEquals("testName", jobEvent.getName());
        Assertions.assertEquals("testAction", jobEvent.getAction());
        Assertions.assertEquals("testCorrelationId", jobEvent.getCorrelationId());
        Assertions.assertEquals(JobState.OK, jobEvent.getState());
    }
}

