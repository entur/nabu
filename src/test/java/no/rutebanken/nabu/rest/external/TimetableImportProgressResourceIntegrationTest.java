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

package no.rutebanken.nabu.rest.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.rutebanken.nabu.BaseIntegrationTest;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.provider.ProviderRepository;
import no.rutebanken.nabu.provider.model.ChouetteInfo;
import no.rutebanken.nabu.provider.model.Provider;
import no.rutebanken.nabu.repository.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.helper.organisation.authorization.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
class TimetableImportProgressResourceIntegrationTest extends BaseIntegrationTest {

    private static final Instant T0 = Instant.parse("2026-09-30T10:30:00Z");

    private static final Long PROVIDER_A = 1L;
    private static final String CODESPACE_A = "RUT";
    private static final Long PROVIDER_B = 2L;
    private static final String CODESPACE_B = "SKY";
    /** Provider A's migration target dataspace. */
    private static final Long PROVIDER_A_MIGRATION_TARGET = 3L;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProviderRepository providerRepository;

    @MockitoBean(name = "authorizationService")
    private AuthorizationService<Long> authorizationService;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/services/events-external/progress/";
        clearEvents();

        when(providerRepository.getProvider(PROVIDER_A)).thenReturn(provider(PROVIDER_A, CODESPACE_A));
        when(providerRepository.getProvider(PROVIDER_B)).thenReturn(provider(PROVIDER_B, CODESPACE_B));
        // The migration target dataspace belongs to the same organisation, so it carries the same
        // codespace as the originating provider.
        when(providerRepository.getProvider(PROVIDER_A_MIGRATION_TARGET)).thenReturn(provider(PROVIDER_A_MIGRATION_TARGET, CODESPACE_A));
    }

    @AfterEach
    void tearDown() {
        clearEvents();
    }

    private void clearEvents() {
        transactionTemplate.execute(status -> {
            eventRepository.deleteAll();
            return null;
        });
    }

    private static Provider provider(Long id, String codespace) {
        ChouetteInfo chouetteInfo = new ChouetteInfo();
        chouetteInfo.id = id;
        chouetteInfo.xmlns = codespace;
        return new Provider(id, "Provider " + codespace, chouetteInfo);
    }

    private void saveEvent(String correlationId, Long providerId, String action, JobState state, long secondsAfterT0) {
        saveEvent(JobEvent.JobDomain.TIMETABLE, correlationId, providerId, action, state, secondsAfterT0);
    }

    private void saveEvent(JobEvent.JobDomain domain, String correlationId, Long providerId, String action, JobState state, long secondsAfterT0) {
        transactionTemplate.execute(status -> {
            JobEvent event = new JobEvent(domain.toString(), "file.zip", providerId,
                    null, action, state, correlationId, T0.plusSeconds(secondsAfterT0), "ost");
            eventRepository.save(event);
            return null;
        });
    }

    private ResponseEntity<String> get(String correlationId) {
        return restTemplate.getForEntity(baseUrl + correlationId, String.class);
    }

    // ---- Authorization -------------------------------------------------------------------------

    /**
     * The caller holds rights for codespace A; the correlation id belongs to codespace B. Because
     * the codespace is resolved from the events rather than taken from the request, there is no
     * request the caller can construct that puts their own codespace in front of someone else's
     * import.
     */
    @Test
    void aCallerAskingAboutAnotherCodespacesImportIsRefused() {
        String correlationId = "corr-belonging-to-b";
        saveEvent(correlationId, PROVIDER_B, "FILE_TRANSFER", JobState.OK, 0);

        when(authorizationService.canEditRouteData(PROVIDER_A)).thenReturn(true);
        when(authorizationService.canEditRouteData(PROVIDER_B)).thenReturn(false);

        ResponseEntity<String> response = get(correlationId);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(),
                "must be 403, not 404 and above all not data");
        assertFalse(String.valueOf(response.getBody()).contains("FILE_TRANSFER"),
                "no stage data may leak into the refusal, body was: " + response.getBody());
    }

    @Test
    void aCallerEntitledToTheOwningCodespaceGetsTheProgress() {
        String correlationId = "corr-belonging-to-a";
        saveEvent(correlationId, PROVIDER_A, "FILE_TRANSFER", JobState.OK, 0);

        when(authorizationService.canEditRouteData(PROVIDER_A)).thenReturn(true);

        ResponseEntity<String> response = get(correlationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * An unknown correlation id is 404 for everyone, entitled or not. Asserted so that a later change
     * to return 403 here is a deliberate one rather than a drift.
     */
    @Test
    void anUnknownCorrelationIdIsNotFoundForAnyone() {
        when(authorizationService.canEditRouteData(PROVIDER_A)).thenReturn(true);
        assertEquals(HttpStatus.NOT_FOUND, get("no-such-correlation-id").getStatusCode());

        when(authorizationService.canEditRouteData(PROVIDER_A)).thenReturn(false);
        assertEquals(HttpStatus.NOT_FOUND, get("no-such-correlation-id").getStatusCode());
    }

    /**
     * An import that crossed into a migration target dataspace still records the originating
     * provider on its events, so it authorizes against the originating codespace.
     */
    @Test
    void anImportThatCrossedIntoAMigrationDataspaceIsAuthorizedAgainstTheOriginatingCodespace() {
        String correlationId = "corr-with-dataspace-transfer";
        saveEvent(correlationId, PROVIDER_A, "FILE_TRANSFER", JobState.OK, 0);
        saveEvent(correlationId, PROVIDER_A_MIGRATION_TARGET, "DATASPACE_TRANSFER", JobState.OK, 10);

        when(authorizationService.canEditRouteData(PROVIDER_A)).thenReturn(true);
        when(authorizationService.canEditRouteData(PROVIDER_A_MIGRATION_TARGET)).thenReturn(true);

        ResponseEntity<String> response = get(correlationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ---- Response ------------------------------------------------------------------------------

    /**
     * An import still in flight: everything up to the export has finished and the shared graph build
     * is queued. Mirrors the "inProgress" example in the OpenAPI spec.
     */
    @Test
    void returnsOneElementPerObservedStageOrderedByFirstEvent() throws Exception {
        String correlationId = "corr-import-in-flight";
        saveEvent(correlationId, PROVIDER_A, "FILE_TRANSFER", JobState.STARTED, 0);
        saveEvent(correlationId, PROVIDER_A, "FILE_TRANSFER", JobState.OK, 4);
        saveEvent(correlationId, PROVIDER_A, "PREVALIDATION", JobState.STARTED, 5);
        saveEvent(correlationId, PROVIDER_A, "PREVALIDATION", JobState.OK, 252);
        saveEvent(correlationId, PROVIDER_A, "EXPORT_NETEX", JobState.STARTED, 253);
        saveEvent(correlationId, PROVIDER_A, "EXPORT_NETEX", JobState.OK, 408);
        saveEvent(correlationId, PROVIDER_A, "OTP2_BUILD_GRAPH", JobState.PENDING, 409);

        when(authorizationService.canEditRouteData(PROVIDER_A)).thenReturn(true);

        ResponseEntity<String> response = get(correlationId);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JsonNode body = objectMapper.readTree(response.getBody());
        assertEquals(CODESPACE_A, body.get("codespace").asText());
        assertEquals(correlationId, body.get("correlationId").asText());

        JsonNode stages = body.get("stages");
        assertEquals(4, stages.size());

        assertEquals("FILE_TRANSFER", stages.get(0).get("stage").asText());
        assertEquals("COMPLETED", stages.get(0).get("status").asText());
        assertEquals("2026-09-30T10:30:00Z", stages.get(0).get("startedAt").asText());
        assertEquals("2026-09-30T10:30:04Z", stages.get(0).get("completedAt").asText());
        assertTrue(stages.get(0).get("errorCode").isNull());

        assertEquals("PREVALIDATION", stages.get(1).get("stage").asText());
        assertEquals("COMPLETED", stages.get(1).get("status").asText());

        assertEquals("NETEX_EXPORT", stages.get(2).get("stage").asText());
        assertEquals("COMPLETED", stages.get(2).get("status").asText());

        assertEquals("GRAPH_BUILD", stages.get(3).get("stage").asText());
        assertEquals("PENDING", stages.get(3).get("status").asText());
        assertTrue(stages.get(3).get("startedAt").isNull());
        assertTrue(stages.get(3).get("completedAt").isNull());
    }

    /**
     * An import stopped by a failed validation. Nothing follows the failed stage, because the
     * pipeline never started anything else. Mirrors the "failedPrevalidation" example in the spec.
     */
    @Test
    void reportsTheErrorCodeOfTheStageThatStoppedTheImport() throws Exception {
        String correlationId = "corr-failed-prevalidation";
        saveEvent(correlationId, PROVIDER_A, "FILE_TRANSFER", JobState.STARTED, 0);
        saveEvent(correlationId, PROVIDER_A, "FILE_TRANSFER", JobState.OK, 4);
        saveEvent(correlationId, PROVIDER_A, "PREVALIDATION", JobState.STARTED, 5);
        transactionTemplate.execute(status -> {
            JobEvent failed = new JobEvent(JobEvent.JobDomain.TIMETABLE.toString(), "file.zip", PROVIDER_A,
                    null, "PREVALIDATION", JobState.FAILED, correlationId, T0.plusSeconds(252), "ost");
            failed.setErrorCode("ERROR_FILE_INVALID_XML_CONTENT");
            eventRepository.save(failed);
            return null;
        });

        when(authorizationService.canEditRouteData(PROVIDER_A)).thenReturn(true);

        JsonNode stages = objectMapper.readTree(get(correlationId).getBody()).get("stages");

        assertEquals(2, stages.size());
        assertEquals("FILE_TRANSFER", stages.get(0).get("stage").asText());
        assertTrue(stages.get(0).get("errorCode").isNull());
        assertEquals("PREVALIDATION", stages.get(1).get("stage").asText());
        assertEquals("FAILED", stages.get(1).get("status").asText());
        assertEquals("ERROR_FILE_INVALID_XML_CONTENT", stages.get(1).get("errorCode").asText());
    }

    /**
     * A cancelled stage is not exposed at all, so a provider with auto-import disabled sees the
     * transfer and nothing after it.
     */
    @Test
    void aCancelledStageIsAbsentFromTheResponse() throws Exception {
        String correlationId = "corr-auto-import-disabled";
        saveEvent(correlationId, PROVIDER_A, "FILE_TRANSFER", JobState.OK, 0);
        saveEvent(correlationId, PROVIDER_A, "IMPORT", JobState.CANCELLED, 1);

        when(authorizationService.canEditRouteData(PROVIDER_A)).thenReturn(true);

        JsonNode stages = objectMapper.readTree(get(correlationId).getBody()).get("stages");

        assertEquals(1, stages.size());
        assertEquals("FILE_TRANSFER", stages.get(0).get("stage").asText());
    }

    /**
     * An event recorded under a domain unrelated to a timetable import must not be picked up just
     * because it happens to share the correlation id.
     */
    @Test
    void anEventFromAnUnrelatedDomainIsNotIncluded() throws Exception {
        String correlationId = "corr-with-unrelated-domain-event";
        saveEvent(correlationId, PROVIDER_A, "FILE_TRANSFER", JobState.OK, 0);
        saveEvent(JobEvent.JobDomain.TIAMAT, correlationId, PROVIDER_A, "EXPORT", JobState.OK, 1);

        when(authorizationService.canEditRouteData(PROVIDER_A)).thenReturn(true);

        JsonNode stages = objectMapper.readTree(get(correlationId).getBody()).get("stages");

        assertEquals(1, stages.size());
        assertEquals("FILE_TRANSFER", stages.get(0).get("stage").asText());
    }

    /**
     * A timeout reports FAILED, and carries no reason because a timed-out stage never gave one.
     */
    @Test
    void aTimedOutStageReportsFailedWithoutAnErrorCode() throws Exception {
        String correlationId = "corr-timed-out";
        saveEvent(correlationId, PROVIDER_A, "IMPORT", JobState.STARTED, 0);
        saveEvent(correlationId, PROVIDER_A, "IMPORT", JobState.TIMEOUT, 3600);

        when(authorizationService.canEditRouteData(PROVIDER_A)).thenReturn(true);

        JsonNode stages = objectMapper.readTree(get(correlationId).getBody()).get("stages");

        assertEquals(1, stages.size());
        assertEquals("FAILED", stages.get(0).get("status").asText());
        assertTrue(stages.get(0).get("errorCode").isNull());
    }

    /**
     * Pub/Sub delivers a service's start and finish messages out of order often enough that this
     * matters: without the guard the stage would report as still running.
     */
    @Test
    void aStaleStartedEventDoesNotMakeAFinishedStageLookInProgress() throws Exception {
        String correlationId = "corr-out-of-order";
        saveEvent(correlationId, PROVIDER_A, "LINKING", JobState.OK, 2);
        saveEvent(correlationId, PROVIDER_A, "LINKING", JobState.STARTED, 3);

        when(authorizationService.canEditRouteData(PROVIDER_A)).thenReturn(true);

        JsonNode stages = objectMapper.readTree(get(correlationId).getBody()).get("stages");

        assertEquals(1, stages.size());
        assertEquals("COMPLETED", stages.get(0).get("status").asText());
        // The STARTED was dropped as stale, so the stage falls back to the time it finished.
        assertEquals("2026-09-30T10:30:02Z", stages.get(0).get("startedAt").asText());
        assertEquals("2026-09-30T10:30:02Z", stages.get(0).get("completedAt").asText());
    }
}
