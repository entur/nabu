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

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.GeoCoderAction;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.domain.event.NotificationType;
import no.rutebanken.nabu.domain.event.TimeTableAction;
import no.rutebanken.nabu.event.ScheduledNotificationService;
import no.rutebanken.nabu.event.filter.EventMatcher;
import no.rutebanken.nabu.event.user.dto.user.EventFilterDTO;
import no.rutebanken.nabu.repository.NotificationRepository;
import no.rutebanken.nabu.rest.domain.ApiCrudEvent;
import no.rutebanken.nabu.rest.domain.ApiJobEvent;
import no.rutebanken.nabu.rest.domain.ApiNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Produces("application/json")
@Path("notifications")
@Tags(value = {
        @Tag(name = "NotificationResource", description ="Notification resource")
})
@PreAuthorize("@authorizationService.isOrganizationAdmin()")
public class NotificationResource {

    @Autowired
    private NotificationRepository notificationRepository;

    @GET
    @Path("/{userName}")
    @PreAuthorize("#userName == authentication.name")
    public List<ApiNotification> getWebNotificationsForUser(@PathParam("userName") String userName) {
        List<Notification> notifications = notificationRepository.findByUserNameAndTypeAndStatus(userName, NotificationType.WEB, Notification.NotificationStatus.READY);
        return notifications.stream().map(this::toDTO).toList();
    }


    @POST
    @Path("/{userName}/mark_read")
    @Transactional
    @PreAuthorize("#userName == authentication.name")
    public void markAsRead(@PathParam("userName") String userName, List<Long> notificationPks) {
        if (!CollectionUtils.isEmpty(notificationPks)) {
            List<Notification> notifications = notificationPks.stream().map(pk -> notificationRepository.getReferenceById(pk)).filter(n -> n.getUserName().equals(userName)).toList();

            notifications.forEach(n -> n.setStatus(Notification.NotificationStatus.COMPLETE));
            notificationRepository.saveAll(notifications);
        }
    }


    // TODO tmp service until scheduling?

    @Autowired
    private ScheduledNotificationService scheduledNotificationService;

    @POST
    @Path("/email")
    public void sendEmails() {
        scheduledNotificationService.sendNotifications(NotificationType.EMAIL_BATCH);
    }


    @GET
    @Path("notification_types")
    public NotificationType[] getNotificationTypes() {
        return NotificationType.values();
    }

    @GET
    @Path("job_domains")
    public JobEvent.JobDomain[] getJobDomains() {
        return JobEvent.JobDomain.values();
    }

    @GET
    @Path("job_states")
    public JobState[] getJobStates() {
        return JobState.values();
    }


    @GET
    @Path("event_filter_types")
    public EventFilterDTO.EventFilterType[] getEventFilterTypes() {
        return EventFilterDTO.EventFilterType.values();
    }

    @GET
    @Path("job_actions/{jobDomain}")
    public List<String> getJobActions(@PathParam("jobDomain") JobEvent.JobDomain jobDomain) {
        List<String> actions = new ArrayList<>(List.of(EventMatcher.ALL_TYPES));

        if (JobEvent.JobDomain.GRAPH.equals(jobDomain)) {
            actions.addAll(Arrays.asList("BUILD_BASE", "BUILD_GRAPH"));
        } else if (JobEvent.JobDomain.TIAMAT.equals(jobDomain)) {
            actions.add("EXPORT");
        } else if (JobEvent.JobDomain.GEOCODER.equals(jobDomain)) {
            actions.addAll(Arrays.stream(GeoCoderAction.values()).map(Enum::name).toList());
        } else if (JobEvent.JobDomain.TIMETABLE.equals(jobDomain)) {
            actions.addAll(Arrays.stream(TimeTableAction.values()).map(Enum::name).toList());
        } else if (JobEvent.JobDomain.TIMETABLE_PUBLISH.equals(jobDomain)) {
            actions.addAll(Arrays.asList("EXPORT_NETEX_MERGED", "EXPORT_GOOGLE_GTFS"));
        } else {
            throw new EntityNotFoundException("Unknown job domain: " + jobDomain);
        }
        return actions;
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
