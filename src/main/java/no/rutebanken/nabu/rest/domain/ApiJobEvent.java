package no.rutebanken.nabu.rest.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;

import java.time.Instant;

/**
 * JobEvent model for API usage.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiJobEvent {

    public Instant registeredTime;

    public Instant eventTime;

    public String correlationId;

    public String domain;
    public String action;
    public JobState state;


    public String externalId;

    public Long providerId;

    public String referential;

    public String name;


    public static ApiJobEvent fromJobEvent(JobEvent jobEvent) {
        ApiJobEvent apiJobEvent = new ApiJobEvent();
        apiJobEvent.eventTime = jobEvent.getEventTime();
        apiJobEvent.registeredTime = jobEvent.getRegisteredTime();

        apiJobEvent.action = jobEvent.getAction();
        apiJobEvent.externalId = jobEvent.getExternalId();
        apiJobEvent.name = jobEvent.getName();
        apiJobEvent.providerId = jobEvent.getProviderId();
        apiJobEvent.referential = jobEvent.getReferential();
        apiJobEvent.state = jobEvent.getState();
        apiJobEvent.domain = jobEvent.getDomain();
        return apiJobEvent;
    }
}

