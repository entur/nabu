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

package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.BaseIntegrationTest;
import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.TimeTableAction;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.repository.SystemJobStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class RestApiIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SystemJobStatusRepository systemJobStatusRepository;

    @Autowired
    private EventRepository eventRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void testServerIsRunningAndResponding() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events/admin_summary/status/aggregation", 
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testInternalAdminSummaryEndpointReturnsData() {
        SystemJobStatus status = new SystemJobStatus("TEST_DOMAIN", "TEST_ACTION", JobState.OK, Instant.now());
        systemJobStatusRepository.save(status);
        systemJobStatusRepository.flush();

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events/admin_summary/status/aggregation",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void testInternalAdminSummaryEndpointWithMultipleStatuses() {
        SystemJobStatus status1 = new SystemJobStatus("TIMETABLE_DOMAIN", "IMPORT_ACTION", JobState.OK, Instant.now());
        SystemJobStatus status2 = new SystemJobStatus("TIMETABLE_DOMAIN", "EXPORT_ACTION", JobState.STARTED, Instant.now());
        systemJobStatusRepository.save(status1);
        systemJobStatusRepository.save(status2);
        systemJobStatusRepository.flush();

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events/admin_summary/status/aggregation",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void testInternalLatestUploadEndpointWithData() {
        Long providerId = 1L;
        
        JobEvent event = new JobEvent();
        event.setDomain("timetable");
        event.setAction(TimeTableAction.FILE_TRANSFER.toString());
        event.setState(JobState.OK);
        event.setCorrelationId("test-correlation-upload");
        event.setProviderId(providerId);
        event.setName("upload-test.xml");
        event.setEventTime(Instant.now());
        eventRepository.save(event);
        eventRepository.flush();

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events/latest_upload/" + providerId,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testExternalOpenApiEndpoint() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events-external/openapi.json",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("openapi") || response.getBody().contains("paths"));
    }

    @Test
    void testInternalJerseyServletIsRegistered() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events/admin_summary/status/aggregation",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testExternalJerseyServletIsRegistered() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events-external/openapi.json",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
