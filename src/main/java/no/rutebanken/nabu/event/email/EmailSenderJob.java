package no.rutebanken.nabu.event.email;

import no.rutebanken.nabu.event.NotificationService;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Quartz job trigger email notifications.
 */
public class EmailSenderJob implements Job {

    @Autowired
    private NotificationService notificationService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        notificationService.sendNotifications(NotificationType.EMAIL);
    }
}
