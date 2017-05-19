package no.rutebanken.nabu.event.email;

import com.google.common.collect.Sets;
import no.rutebanken.nabu.NabuTestApp;
import no.rutebanken.nabu.domain.Provider;
import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.Notification;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NabuTestApp.class)
public class EmailNotificationFormatterTest {

    @Autowired
    private EmailNotificationFormatter emailNotificationFormatter;
    private List<Provider> providerList = Arrays.asList(new Provider(1l, "ProviderName", null, null));

    @Test
    public void formatMailInNorwegian() {
        Set<Notification> notifications = Sets.newHashSet(jobNotification(), crudNotification(Instant.now()));

        String msg = emailNotificationFormatter.formatMessage(notifications, new Locale("no"), providerList);
        System.out.println(msg);
        Assert.assertTrue(msg.startsWith("<html>"));
        Assert.assertFalse("Expected all message keys to have been resolved", msg.contains("notification.email"));
        Assert.assertTrue(msg.contains("varsling"));   // TODO norwegian still missing lots of values. How do we verify?

        Assert.assertTrue("Should be able to map providerId to name", msg.contains(providerList.get(0).getName()));
    }

    @Test
    public void formatMailWithTooManyEvents() {
        Instant now = Instant.now();

        Set<Notification> notifications = Sets.newHashSet(jobNotification(), crudNotification(now.minusMillis(1000)), crudNotification(now.minusMillis(2000)),
                crudNotification(now.minusMillis(3000)), crudNotification(now.minusMillis(4000)));

        Notification oldestEvent = crudNotification(now.minusMillis(5000));
        oldestEvent.getEvent().setName("nameShouldNotBeInEmail");
        notifications.add(oldestEvent);

        String msg = emailNotificationFormatter.formatMessage(notifications, new Locale("en"), providerList);
        System.out.println(msg);
        Assert.assertTrue(msg.startsWith("<html>"));
        Assert.assertFalse("Expected all message keys to have been resolved", msg.contains("notification.email"));
        Assert.assertTrue(msg.contains("Too many events have been registered in the period (6). Only the 5 newest events are included"));

        Assert.assertFalse("Expected oldest event to be omitted", msg.contains(oldestEvent.getEvent().getName()));
    }


    private Notification jobNotification() {
        Notification notification = new Notification();

        JobEvent event = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).state(JobState.FAILED).providerId(providerList.get(0).id).referential("ref").action("IMPORT").name("fileName.xml").eventTime(Instant.now()).build();
        event.setPk(pkCounter++);
        notification.setEvent(event);

        return notification;
    }

    long pkCounter = 1;

    private Notification crudNotification(Instant time) {
        Notification notification = new Notification();
        CrudEvent event = CrudEvent.builder().entityType("StopPlace").entityClassifier("onstreetBus").version(1l).changeType("NAME").oldValue("Old name").newValue("Hakkadal").action("CREATE").name("Hakkadal").eventTime(time).build();
        event.setPk(pkCounter++);
        notification.setEvent(event);

        return notification;
    }
}
