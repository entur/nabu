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

import com.fasterxml.jackson.databind.ObjectMapper;
import no.rutebanken.nabu.BaseIntegrationTest;
import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.TimeTableAction;
import no.rutebanken.nabu.provider.ProviderRepository;
import no.rutebanken.nabu.provider.model.ChouetteInfo;
import no.rutebanken.nabu.provider.model.Provider;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.repository.SystemJobStatusRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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

    @MockitoBean
    private ProviderRepository providerRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        cleanupTestData();
        setupMockProvider();
    }

    private void setupMockProvider() {
        // Set up a test provider with codespace "TEST"
        ChouetteInfo chouetteInfo = new ChouetteInfo();
        chouetteInfo.id = 1L;
        chouetteInfo.xmlns = "TEST"; // This must match the codespace used in tests
        chouetteInfo.migrateDataToProvider = 2L; // Migrates data to provider 2

        Provider testProvider = new Provider(1L, "Test Provider", chouetteInfo);

        when(providerRepository.getProvider(anyString())).thenReturn(testProvider);
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
        assertTrue(!jsonArray.isEmpty(), "Should have at least one aggregation");

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

    @Test
    void testTimetableDataDeliveryStatusWithSuccessfulDelivery() throws Exception {
        String codespace = "ent";
        String correlationId = "test-correlation-123";
        String testFileName = "test-file.xml";
        Instant baseTime = Instant.now();

        transactionTemplate.execute(status -> {
            // Create FILE_TRANSFER event for both provider IDs
            JobEvent fileTransferEvent = new JobEvent();
            fileTransferEvent.setDomain(JobEvent.JobDomain.TIMETABLE.toString());
            fileTransferEvent.setAction(TimeTableAction.FILE_TRANSFER.toString());
            fileTransferEvent.setState(JobState.OK);
            fileTransferEvent.setCorrelationId(correlationId);
            fileTransferEvent.setProviderId(1L); // Main provider ID
            fileTransferEvent.setName(testFileName);
            fileTransferEvent.setEventTime(baseTime);
            eventRepository.save(fileTransferEvent);

            // Create OTP2_BUILD_GRAPH event to mark delivery as complete
            JobEvent buildGraphEvent = new JobEvent();
            buildGraphEvent.setDomain(JobEvent.JobDomain.TIMETABLE.toString());
            buildGraphEvent.setAction(TimeTableAction.OTP2_BUILD_GRAPH.toString());
            buildGraphEvent.setState(JobState.OK);
            buildGraphEvent.setCorrelationId(correlationId);
            buildGraphEvent.setProviderId(1L);
            buildGraphEvent.setName(testFileName);
            buildGraphEvent.setEventTime(baseTime.plusSeconds(60));
            eventRepository.save(buildGraphEvent);

            return null;
        });

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events-external/status/" + codespace + "/" + correlationId,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        var jsonResponse = objectMapper.readTree(response.getBody());

        // Verify response structure
        assertTrue(jsonResponse.has("state"), "Should have 'state' field");
        assertTrue(jsonResponse.has("date"), "Should have 'date' field");
        assertTrue(jsonResponse.has("fileName"), "Should have 'fileName' field");

        // Verify delivery completed successfully
        assertEquals("OK", jsonResponse.get("state").asText(), "State should be OK when OTP2_BUILD_GRAPH succeeds");
        assertEquals(testFileName, jsonResponse.get("fileName").asText(), "File name should match");
    }

    @Test
    void testTimetableDataDeliveryStatusWithFailedDelivery() throws Exception {
        String codespace = "TEST";
        String correlationId = "test-correlation-failed-456";
        String testFileName = "failed-file.xml";
        Instant baseTime = Instant.now();

        transactionTemplate.execute(status -> {
            // Create FILE_TRANSFER event
            JobEvent fileTransferEvent = new JobEvent();
            fileTransferEvent.setDomain(JobEvent.JobDomain.TIMETABLE.toString());
            fileTransferEvent.setAction(TimeTableAction.FILE_TRANSFER.toString());
            fileTransferEvent.setState(JobState.OK);
            fileTransferEvent.setCorrelationId(correlationId);
            fileTransferEvent.setProviderId(1L);
            fileTransferEvent.setName(testFileName);
            fileTransferEvent.setEventTime(baseTime);
            eventRepository.save(fileTransferEvent);

            // Create IMPORT event with FAILED state
            JobEvent importEvent = new JobEvent();
            importEvent.setDomain(JobEvent.JobDomain.TIMETABLE.toString());
            importEvent.setAction(TimeTableAction.IMPORT.toString());
            importEvent.setState(JobState.FAILED);
            importEvent.setCorrelationId(correlationId);
            importEvent.setProviderId(1L);
            importEvent.setName(testFileName);
            importEvent.setEventTime(baseTime.plusSeconds(30));
            eventRepository.save(importEvent);

            return null;
        });

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events-external/status/" + codespace + "/" + correlationId,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        var jsonResponse = objectMapper.readTree(response.getBody());

        // Verify response structure
        assertTrue(jsonResponse.has("state"), "Should have 'state' field");
        assertEquals("FAILED", jsonResponse.get("state").asText(), "State should be FAILED when any event fails");
        assertEquals(testFileName, jsonResponse.get("fileName").asText(), "File name should match");
    }

    @Test
    void testTimetableDataDeliveryStatusInProgress() throws Exception {
        String codespace = "TEST";
        String correlationId = "test-correlation-progress-789";
        String testFileName = "in-progress-file.xml";
        Instant baseTime = Instant.now();

        transactionTemplate.execute(status -> {
            // Create FILE_TRANSFER event
            JobEvent fileTransferEvent = new JobEvent();
            fileTransferEvent.setDomain(JobEvent.JobDomain.TIMETABLE.toString());
            fileTransferEvent.setAction(TimeTableAction.FILE_TRANSFER.toString());
            fileTransferEvent.setState(JobState.OK);
            fileTransferEvent.setCorrelationId(correlationId);
            fileTransferEvent.setProviderId(1L);
            fileTransferEvent.setName(testFileName);
            fileTransferEvent.setEventTime(baseTime);
            eventRepository.save(fileTransferEvent);

            // Create IMPORT event that is still processing
            JobEvent importEvent = new JobEvent();
            importEvent.setDomain(JobEvent.JobDomain.TIMETABLE.toString());
            importEvent.setAction(TimeTableAction.IMPORT.toString());
            importEvent.setState(JobState.STARTED);
            importEvent.setCorrelationId(correlationId);
            importEvent.setProviderId(1L);
            importEvent.setName(testFileName);
            importEvent.setEventTime(baseTime.plusSeconds(15));
            eventRepository.save(importEvent);

            return null;
        });

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events-external/status/" + codespace + "/" + correlationId,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        var jsonResponse = objectMapper.readTree(response.getBody());

        // Verify response structure
        assertTrue(jsonResponse.has("state"), "Should have 'state' field");
        assertEquals("IN_PROGRESS", jsonResponse.get("state").asText(),
                "State should be IN_PROGRESS when delivery is not complete and has no failures");
        assertEquals(testFileName, jsonResponse.get("fileName").asText(), "File name should match");
    }

    @Test
    void testTimetableDataDeliveryStatusNotFound() {
        String codespace = "TEST";
        String nonExistentCorrelationId = "non-existent-correlation-id";

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events-external/status/" + codespace + "/" + nonExistentCorrelationId,
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "Should return 404 when correlation ID is not found");
    }

    @Test
    void testTimetableDataDeliveryStatusWithProviderNotFound() {
        String nonExistentCodespace = "NONEXISTENT";
        String correlationId = "test-correlation";

        // Mock returns null for non-existent provider
        when(providerRepository.getProvider(nonExistentCodespace)).thenReturn(null);

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/services/events-external/status/" + nonExistentCodespace + "/" + correlationId,
                String.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(),
                "Should return 500 when provider is not found");
    }

}
