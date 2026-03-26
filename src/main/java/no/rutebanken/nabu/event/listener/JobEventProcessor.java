package no.rutebanken.nabu.event.listener;

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.event.EventService;
import no.rutebanken.nabu.event.NabuEventValidationException;
import no.rutebanken.nabu.event.listener.dto.JobEventDTO;
import no.rutebanken.nabu.event.listener.mapper.EventMapper;
import no.rutebanken.nabu.exceptions.NabuException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JobEventProcessor {

    private static final String DATASET_REFERENTIAL = "EnturDatasetReferential";
    private static final String CHOUETTE_REFERENTIAL = "RutebankenChouetteReferential";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EventService eventService;
    private final EventMapper eventMapper = new EventMapper();

    public JobEventProcessor(EventService eventService) {
        this.eventService = eventService;
    }

    public void processMessage(String content) {
        processMessage(content, Map.of());
    }

    public void processMessage(String content, Map<String, String> headers) {
        JobEventDTO dto = JobEventDTO.fromString(content);
        try {
            if (dto.getCorrelationId() != null) {
                MDC.put("correlationId", dto.getCorrelationId());
            }
            String codespace = dto.getReferential();
            if (codespace == null || codespace.isEmpty()) {
                codespace = headers.get(DATASET_REFERENTIAL);
            }
            if (codespace == null || codespace.isEmpty()) {
                codespace = headers.get(CHOUETTE_REFERENTIAL);
            }
            if (codespace != null && !codespace.isEmpty()) {
                MDC.put("codespace", codespace);
            }
            Event event = eventMapper.toJobEvent(dto);
            logger.info("Received job event: {}", event);

            try {
                eventService.addEvent(event);
            } catch (NabuEventValidationException e) {
                logger.warn("Skipping job event {} with validation errors: {}", event, e.validationErrors());
            } catch (Exception e) {
                throw new NabuException("Error while saving JobEvent " + event, e);
            }
        } finally {
            MDC.remove("correlationId");
            MDC.remove("codespace");
        }
    }
}
