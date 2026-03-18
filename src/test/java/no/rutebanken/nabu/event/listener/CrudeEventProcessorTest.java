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
import no.rutebanken.nabu.event.EventService;
import no.rutebanken.nabu.event.NabuEventValidationException;
import no.rutebanken.nabu.event.listener.dto.CrudEventDTO;
import no.rutebanken.nabu.exceptions.NabuException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CrudeEventProcessorTest {

    private EventService eventService;
    private CrudeEventProcessor processor;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        processor = new CrudeEventProcessor(eventService);
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void mdcCorrelationIdSetDuringProcessing() {
        AtomicReference<String> captured = new AtomicReference<>();
        doAnswer(invocation -> {
            captured.set(MDC.get("correlationId"));
            return null;
        }).when(eventService).addEvent(any());

        processor.processMessage(toJson(createEvent("corr-123")));

        Assertions.assertEquals("corr-123", captured.get());
    }

    @Test
    void mdcCorrelationIdNotSetWhenNull() {
        AtomicReference<String> captured = new AtomicReference<>("sentinel");
        doAnswer(invocation -> {
            captured.set(MDC.get("correlationId"));
            return null;
        }).when(eventService).addEvent(any());

        processor.processMessage(toJson(createEvent(null)));

        Assertions.assertNull(captured.get());
    }

    @Test
    void mdcClearedAfterSuccessfulProcessing() {
        processor.processMessage(toJson(createEvent("corr-456")));

        Assertions.assertNull(MDC.get("correlationId"));
    }

    @Test
    void mdcClearedAfterValidationException() {
        doThrow(new NabuEventValidationException("validation error", Set.of()))
                .when(eventService).addEvent(any());

        processor.processMessage(toJson(createEvent("corr-789")));

        Assertions.assertNull(MDC.get("correlationId"));
    }

    @Test
    void mdcClearedAfterGeneralException() {
        doThrow(new RuntimeException("something failed"))
                .when(eventService).addEvent(any());

        Assertions.assertThrows(NabuException.class,
                () -> processor.processMessage(toJson(createEvent("corr-abc"))));

        Assertions.assertNull(MDC.get("correlationId"));
    }

    @Test
    void mdcRemovePreservesPreExistingValues() {
        MDC.put("existingKey", "existingValue");

        processor.processMessage(toJson(createEvent("corr-123")));

        Assertions.assertEquals("existingValue", MDC.get("existingKey"));
        Assertions.assertNull(MDC.get("correlationId"));
    }

    private CrudEventDTO createEvent(String correlationId) {
        CrudEventDTO dto = new CrudEventDTO();
        dto.eventTime = Instant.now();
        dto.action = "UPDATE";
        dto.entityType = "StopPlace";
        dto.version = 1L;
        dto.correlationId = correlationId;
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
