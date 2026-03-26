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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.event.EventService;
import no.rutebanken.nabu.event.NabuEventValidationException;
import no.rutebanken.nabu.event.listener.dto.JobEventDTO;
import no.rutebanken.nabu.exceptions.NabuException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JobEventProcessorTest {

    private EventService eventService;
    private JobEventProcessor processor;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        processor = new JobEventProcessor(eventService);
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void mdcSetWithCorrelationIdAndCodespaceDuringProcessing() {
        AtomicReference<String> capturedCorrelationId = new AtomicReference<>();
        AtomicReference<String> capturedCodespace = new AtomicReference<>();
        doAnswer(invocation -> {
            capturedCorrelationId.set(MDC.get("correlationId"));
            capturedCodespace.set(MDC.get("codespace"));
            return null;
        }).when(eventService).addEvent(any());

        processor.processMessage(toJson(createEvent("corr-123", "rb_kok")));

        Assertions.assertEquals("corr-123", capturedCorrelationId.get());
        Assertions.assertEquals("rb_kok", capturedCodespace.get());
    }

    @Test
    void mdcNotSetWhenCorrelationIdAndReferentialAreNull() {
        AtomicReference<String> capturedCorrelationId = new AtomicReference<>("sentinel");
        AtomicReference<String> capturedCodespace = new AtomicReference<>("sentinel");
        doAnswer(invocation -> {
            capturedCorrelationId.set(MDC.get("correlationId"));
            capturedCodespace.set(MDC.get("codespace"));
            return null;
        }).when(eventService).addEvent(any());

        processor.processMessage(toJson(createEvent(null, null)));

        Assertions.assertNull(capturedCorrelationId.get());
        Assertions.assertNull(capturedCodespace.get());
    }

    @ParameterizedTest(name = "codespace fallback: {0}")
    @CsvSource({
            "DTO referential wins over headers,    rb_flt, RutebankenChouetteReferential, flt,  rb_flt",
            "falls back to EnturDatasetReferential,,       EnturDatasetReferential,        flt,  flt",
            "falls back to ChouetteReferential,,           RutebankenChouetteReferential,  flt,  flt",
    })
    void mdcCodespaceHeaderFallback(String description, String dtoReferential, String headerKey, String headerValue, String expected) {
        AtomicReference<String> capturedCodespace = new AtomicReference<>();
        doAnswer(invocation -> {
            capturedCodespace.set(MDC.get("codespace"));
            return null;
        }).when(eventService).addEvent(any());

        processor.processMessage(toJson(createEvent("corr-123", dtoReferential)), Map.of(headerKey, headerValue));

        Assertions.assertEquals(expected, capturedCodespace.get());
    }

    @Test
    void mdcSetWithOnlyCorrelationId() {
        AtomicReference<String> capturedCorrelationId = new AtomicReference<>();
        AtomicReference<String> capturedCodespace = new AtomicReference<>("sentinel");
        doAnswer(invocation -> {
            capturedCorrelationId.set(MDC.get("correlationId"));
            capturedCodespace.set(MDC.get("codespace"));
            return null;
        }).when(eventService).addEvent(any());

        processor.processMessage(toJson(createEvent("corr-only", null)));

        Assertions.assertEquals("corr-only", capturedCorrelationId.get());
        Assertions.assertNull(capturedCodespace.get());
    }

    @Test
    void mdcSetWithOnlyCodespace() {
        AtomicReference<String> capturedCorrelationId = new AtomicReference<>("sentinel");
        AtomicReference<String> capturedCodespace = new AtomicReference<>();
        doAnswer(invocation -> {
            capturedCorrelationId.set(MDC.get("correlationId"));
            capturedCodespace.set(MDC.get("codespace"));
            return null;
        }).when(eventService).addEvent(any());

        processor.processMessage(toJson(createEvent(null, "rb_kok")));

        Assertions.assertNull(capturedCorrelationId.get());
        Assertions.assertEquals("rb_kok", capturedCodespace.get());
    }

    @Test
    void mdcClearedAfterSuccessfulProcessing() {
        processor.processMessage(toJson(createEvent("corr-456", "rb_kok")));

        Assertions.assertNull(MDC.get("correlationId"));
        Assertions.assertNull(MDC.get("codespace"));
    }

    @Test
    void mdcClearedAfterValidationException() {
        doThrow(new NabuEventValidationException("validation error", Set.of()))
                .when(eventService).addEvent(any());

        processor.processMessage(toJson(createEvent("corr-789", "rb_kok")));

        Assertions.assertNull(MDC.get("correlationId"));
        Assertions.assertNull(MDC.get("codespace"));
    }

    @Test
    void mdcClearedAfterGeneralException() {
        doThrow(new RuntimeException("something failed"))
                .when(eventService).addEvent(any());

        String json = toJson(createEvent("corr-abc", "rb_kok"));
        Assertions.assertThrows(NabuException.class,
                () -> processor.processMessage(json));

        Assertions.assertNull(MDC.get("correlationId"));
        Assertions.assertNull(MDC.get("codespace"));
    }

    @Test
    void mdcRemovePreservesPreExistingValues() {
        MDC.put("existingKey", "existingValue");

        processor.processMessage(toJson(createEvent("corr-123", "rb_kok")));

        Assertions.assertEquals("existingValue", MDC.get("existingKey"));
        Assertions.assertNull(MDC.get("correlationId"));
        Assertions.assertNull(MDC.get("codespace"));
    }

    private JobEventDTO createEvent(String correlationId, String referential) {
        JobEventDTO dto = new JobEventDTO();
        dto.setEventTime(Instant.now());
        dto.setState(JobState.PENDING);
        dto.setAction("action");
        dto.setDomain("TIMETABLE");
        dto.setCorrelationId(correlationId);
        dto.setReferential(referential);
        return dto;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
