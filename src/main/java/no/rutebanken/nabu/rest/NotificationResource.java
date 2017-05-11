package no.rutebanken.nabu.rest;

import no.rutebanken.nabu.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Component
@Produces("application/json")
@Path("/notifications")
public class NotificationResource {

    @Autowired
    private NotificationService notificationService;

    @POST
    public void sendNotifications() {
        notificationService.sendNotificationsForAllUsers();
    }

    @POST
    @Path("/{userId}")
    public void sendNotifications(@PathParam("userId") String userId) {
        notificationService.sendNotificationsForUser(userId);
    }
}
