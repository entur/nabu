package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.event.NotificationService;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.repository.NotificationRepository;
import no.rutebanken.nabu.rest.domain.ApiCrudEvent;
import no.rutebanken.nabu.rest.domain.ApiJobEvent;
import no.rutebanken.nabu.rest.domain.ApiNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Produces("application/json")
@Path("/notifications")
public class NotificationResource {

    @Autowired
    private NotificationRepository notificationRepository;

    @GET
    @Path("/{userName}")
    @PreAuthorize("#userName == authentication.name")
    public List<ApiNotification> getWebNotificationsForUser(@PathParam("userName") String userName) {
        List<Notification> notifications = notificationRepository.findByUserNameAndTypeAndStatus(userName, NotificationType.WEB, Notification.NotificationStatus.READY);
        return notifications.stream().map(notification -> toDTO(notification)).collect(Collectors.toList());
    }


    @POST
    @Path("/{userName}/read")
    @Transactional
    @PreAuthorize("#userName == authentication.name")
    public void markAsRead(@PathParam("userName") String userName, List<Long> notificationPks) {
        if (!CollectionUtils.isEmpty(notificationPks)) {
            notificationPks.stream().map(pk -> notificationRepository.getOne(pk)).filter(n -> n.getUserName().equals(userName)).forEach(n -> notificationRepository.delete(n));
        }
    }


    // TODO tmp service until scheduling?

    @Autowired
    private NotificationService notificationService;

    @POST
    @Path("/email")
    public void sendEmails() {
        notificationService.sendNotifications(NotificationType.EMAIL);
    }


    private ApiNotification toDTO(Notification notification) {
        ApiNotification dto = new ApiNotification();
        dto.status = notification.getStatus();
        dto.userName = notification.getUserName();
        dto.id = notification.getPk();
        Event event = notification.getEvent();

        if (event instanceof JobEvent) {
            dto.jobEvent = ApiJobEvent.fromJobEvent((JobEvent) event);
        } else if (event instanceof CrudEvent) {
            dto.crudEvent = ApiCrudEvent.fromCrudEvent((CrudEvent) event);
        }

        return dto;
    }
}
