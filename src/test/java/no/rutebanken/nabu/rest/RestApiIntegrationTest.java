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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.rutebanken.nabu.BaseIntegrationTest;
import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.TimeTableAction;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.repository.SystemJobStatusRepository;
import no.rutebanken.nabu.rest.domain.DataDeliveryStatus;
import no.rutebanken.nabu.rest.domain.SystemStatusAggregation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
class RestApiIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SystemJobStatusRepository systemJobStatusRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        cleanupTestData();
    }

    @AfterEach
    void tearDown() {
        cleanupTestData();
    }

    private void cleanupTestData() {
        transactionTemplate.execute(status -> {
            eventRepository.deleteAll();
            systemJobStatusRepository.deleteAll();
            return null;
        });
    }

    @Test
    void testServerIsRunningAndResponding() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events/admin_summary/status/aggregation", 
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().startsWith("["), "Response should be a JSON array");
    }

    @Test
    void testInternalAdminSummaryEndpointReturnsData() throws Exception {
        transactionTemplate.execute(status -> {
            SystemJobStatus jobStatus = new SystemJobStatus("TEST_DOMAIN", "TEST_ACTION", JobState.OK, Instant.now());
            systemJobStatusRepository.save(jobStatus);
            return null;
        });

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events/admin_summary/status/aggregation",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Parse as JSON and verify structure
        var jsonArray = objectMapper.readTree(response.getBody());
        assertTrue(jsonArray.isArray(), "Response should be a JSON array");
        assertTrue(jsonArray.size() > 0, "Should have at least one aggregation");
        
        var firstAggregation = jsonArray.get(0);
        assertEquals("TEST_DOMAIN", firstAggregation.get("jobDomain").asText());
        assertEquals("TEST_ACTION", firstAggregation.get("jobType").asText());
        assertEquals("OK", firstAggregation.get("currentState").asText());
        assertNotNull(firstAggregation.get("currentStateDate"));
    }

    @Test
    void testInternalAdminSummaryEndpointWithMultipleStatuses() throws Exception {
        transactionTemplate.execute(status -> {
            SystemJobStatus status1 = new SystemJobStatus("TIMETABLE_DOMAIN", "IMPORT_ACTION", JobState.OK, Instant.now());
            SystemJobStatus status2 = new SystemJobStatus("TIMETABLE_DOMAIN", "EXPORT_ACTION", JobState.STARTED, Instant.now());
            systemJobStatusRepository.save(status1);
            systemJobStatusRepository.save(status2);
            return null;
        });

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events/admin_summary/status/aggregation",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        var jsonArray = objectMapper.readTree(response.getBody());
        assertTrue(jsonArray.isArray(), "Response should be a JSON array");
        assertEquals(2, jsonArray.size(), "Should have two aggregations");
        
        // Verify both aggregations are present
        boolean hasImportAction = false;
        boolean hasExportAction = false;
        
        for (var aggregation : jsonArray) {
            String jobDomain = aggregation.get("jobDomain").asText();
            String jobType = aggregation.get("jobType").asText();
            String currentState = aggregation.get("currentState").asText();
            
            if ("TIMETABLE_DOMAIN".equals(jobDomain) && "IMPORT_ACTION".equals(jobType)) {
                hasImportAction = true;
                assertEquals("OK", currentState);
            }
            if ("TIMETABLE_DOMAIN".equals(jobDomain) && "EXPORT_ACTION".equals(jobType)) {
                hasExportAction = true;
                assertEquals("STARTED", currentState);
            }
        }
        
        assertTrue(hasImportAction, "Should contain IMPORT_ACTION");
        assertTrue(hasExportAction, "Should contain EXPORT_ACTION");
    }

    @Test
    void testInternalLatestUploadEndpointWithData() throws Exception {
        Long providerId = 1L;
        String testFileName = "upload-test.xml";
        String testCorrelationId = "test-correlation-upload";
        Instant baseTime = Instant.now();
        
        transactionTemplate.execute(status -> {
            // Create FILE_TRANSFER event (this is used as the anchor for finding latest upload)
            JobEvent fileTransferEvent = new JobEvent();
            fileTransferEvent.setDomain("timetable");
            fileTransferEvent.setAction(TimeTableAction.FILE_TRANSFER.toString());
            fileTransferEvent.setState(JobState.OK);
            fileTransferEvent.setCorrelationId(testCorrelationId);
            fileTransferEvent.setProviderId(providerId);
            fileTransferEvent.setName(testFileName);
            fileTransferEvent.setEventTime(baseTime);
            eventRepository.save(fileTransferEvent);
            
            // Create OTP2_BUILD_GRAPH event to complete the delivery (same correlation ID)
            JobEvent buildGraphEvent = new JobEvent();
            buildGraphEvent.setDomain("timetable");
            buildGraphEvent.setAction(TimeTableAction.OTP2_BUILD_GRAPH.toString());
            buildGraphEvent.setState(JobState.OK);
            buildGraphEvent.setCorrelationId(testCorrelationId); // Same correlation ID
            buildGraphEvent.setProviderId(providerId);
            buildGraphEvent.setName(testFileName);
            buildGraphEvent.setEventTime(baseTime.plusSeconds(10));
            eventRepository.save(buildGraphEvent);
            
            return null;
        });

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events/latest_upload/" + providerId,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Parse as JSON and verify structure (the response may return nulls if no data found)
        var jsonResponse = objectMapper.readTree(response.getBody());
        
        // Response should have the expected fields
        assertTrue(jsonResponse.has("state"), "Should have 'state' field");
        assertTrue(jsonResponse.has("date"), "Should have 'date' field");
        assertTrue(jsonResponse.has("fileName"), "Should have 'fileName' field");
    }

    @Test
    void testExternalOpenApiEndpoint() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events-external/openapi.json",
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Parse as JSON to verify it's valid JSON
        var openApiSpec = objectMapper.readTree(response.getBody());
        
        // Verify basic OpenAPI structure
        assertTrue(openApiSpec.has("openapi") || openApiSpec.has("swagger"), 
                "Should have 'openapi' or 'swagger' field");
        assertTrue(openApiSpec.has("paths"), 
                "Should have 'paths' field");
        assertTrue(openApiSpec.has("info"), 
                "Should have 'info' field");
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
