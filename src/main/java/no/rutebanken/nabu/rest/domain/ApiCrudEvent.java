package no.rutebanken.nabu.rest.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import no.rutebanken.nabu.domain.event.CrudEvent;

import java.time.Instant;

/**
 * CrudEvent model for API usage.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiCrudEvent {
    public Instant registeredTime;

    public Instant eventTime;

    public String entityType;

    public String entityClassifier;

    public String action;

    public String externalId;

    public Long version;

    public String name;

    public String changeType;

    public String oldValue;

    public String newValue;

    public String comment;


    public static ApiCrudEvent fromCrudEvent(CrudEvent crudEvent) {
        ApiCrudEvent apiCrudEvent = new ApiCrudEvent();
        apiCrudEvent.eventTime = crudEvent.getEventTime();
        apiCrudEvent.registeredTime = crudEvent.getRegisteredTime();
        apiCrudEvent.entityType = crudEvent.getEntityType();
        apiCrudEvent.entityClassifier = crudEvent.getEntityClassifier();
        apiCrudEvent.action = crudEvent.getAction();
        apiCrudEvent.changeType = crudEvent.getChangeType();
        apiCrudEvent.externalId = crudEvent.getExternalId();
        apiCrudEvent.version = crudEvent.getVersion();
        apiCrudEvent.name = crudEvent.getName();
        apiCrudEvent.oldValue = crudEvent.getOldValue();
        apiCrudEvent.newValue = crudEvent.getNewValue();
        apiCrudEvent.comment = crudEvent.getComment();
        return apiCrudEvent;
    }

}
