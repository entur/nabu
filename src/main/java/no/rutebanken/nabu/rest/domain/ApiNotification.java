package no.rutebanken.nabu.rest.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import no.rutebanken.nabu.domain.event.Notification;

/**
 * Notification model for API usage.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiNotification {

    public Long id;

    public String userName;

    public Notification.NotificationStatus status;

    public ApiJobEvent jobEvent;

    public ApiCrudEvent crudEvent;

}
