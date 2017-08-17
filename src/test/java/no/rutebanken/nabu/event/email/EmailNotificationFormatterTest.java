package no.rutebanken.nabu.event.email;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Sets;

import no.rutebanken.nabu.NabuTestApp;
import no.rutebanken.nabu.domain.Provider;
import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.Notification;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NabuTestApp.class)
public class EmailNotificationFormatterTest {

    @Autowired
    private EmailNotificationFormatter emailNotificationFormatter;
    private List<Provider> providerList = Arrays.asList(new Provider(1l, "ProviderName", null, null));

    @Test
    public void formatMailInNorwegian() throws FileNotFoundException {
        Set<Notification> notifications = Sets.newHashSet(jobNotification("file.xml"), maxCrudNotification("NSR:StopPlace:16688", Instant.now()));

        String msg = emailNotificationFormatter.formatMessage(notifications, new Locale("no"), providerList);
        System.out.println(msg);
        PrintWriter out = new PrintWriter("target/email.html");
        out.write(msg);
        out.close();
        Assert.assertTrue(msg.startsWith("<html>"));
        Assert.assertFalse("Expected all message keys to have been resolved", msg.contains("notification.email"));
        Assert.assertTrue(msg.contains("hendelser"));   // TODO norwegian still missing lots of values. How do we verify?

        Assert.assertTrue("Should be able to map providerId to name", msg.contains(providerList.get(0).getName()));
    }

    @Test
    public void formatMailWithTooManyEvents() {
        Instant now = Instant.now();

        Set<Notification> notifications = Sets.newHashSet(jobNotification(null), maxCrudNotification("NSR:StopPlace:2", now.minusMillis(1000)), maxCrudNotification("NSR:StopPlace:1", now.minusMillis(2000)),
                minCrudNotification("NSR:StopPlace:2", now.minusMillis(3000)), maxCrudNotification("NSR:StopPlace:3", now.minusMillis(4000)));

        Notification oldestEvent = maxCrudNotification("NSR:StopPlace:2", now.minusMillis(5000));
        oldestEvent.getEvent().setName("nameShouldNotBeInEmail");
        notifications.add(oldestEvent);

        String msg = emailNotificationFormatter.formatMessage(notifications, new Locale("en"), providerList);
        System.out.println(msg);
        Assert.assertTrue(msg.startsWith("<html>"));
        Assert.assertFalse("Expected all message keys to have been resolved", msg.contains("notification.email"));
        Assert.assertTrue(msg.contains("Too many events have been registered in the period (6). Only the 5 newest events are included"));

        Assert.assertFalse("Expected oldest event to be omitted", msg.contains(oldestEvent.getEvent().getName()));
    }


    private Notification jobNotification(String fileName) {
        Notification notification = new Notification();

        JobEvent event = JobEvent.builder().domain(JobEvent.JobDomain.TIMETABLE).state(JobState.FAILED).providerId(providerList.get(0).id).referential("rb_bra").action("IMPORT").externalId("3209").name(fileName).eventTime(Instant.now()).build();
        event.setPk(pkCounter++);
        notification.setEvent(event);

        return notification;
    }

    long pkCounter = 1;

    private Notification minCrudNotification(String id, Instant time) {
        Notification notification = new Notification();
        CrudEvent event = CrudEvent.builder().entityType("StopPlace").version(1l).eventTime(time).build();
        event.setPk(pkCounter++);
        notification.setEvent(event);

        return notification;
    }

    private Notification maxCrudNotification(String id, Instant time) {
        Notification notification = new Notification();
        CrudEvent event = CrudEvent.builder().entityType("StopPlace").entityClassifier("onstreetBus").version(1l).comment("comment").changeType("NAME").oldValue("Old name").newValue("Hakkadal").username("User e").action("CREATE").name("Hakkadal").externalId(id).eventTime(time).build();
        event.setPk(pkCounter++);
        notification.setEvent(event);

        return notification;
    }
}
