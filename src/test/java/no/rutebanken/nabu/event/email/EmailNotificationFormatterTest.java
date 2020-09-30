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

package no.rutebanken.nabu.event.email;

import no.rutebanken.nabu.BaseIntegrationTest;
import no.rutebanken.nabu.domain.event.CrudEvent;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.domain.event.Notification;
import no.rutebanken.nabu.provider.model.Provider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class EmailNotificationFormatterTest extends BaseIntegrationTest {

    @Autowired
    private EmailNotificationFormatter emailNotificationFormatter;

    @Autowired
    private MessageSource messageSource;

    private List<Provider> providerList = Collections.singletonList(new Provider(1011L, "ProviderName", null));

    @Test
    public void formatMailInNorwegian() throws FileNotFoundException {
        Set<Notification> notifications = Set.of(jobNotification("file.xml"), maxCrudNotification("NSR:StopPlace:16688", Instant.now()));

        String msg = emailNotificationFormatter.formatMessage(notifications, new Locale("no"), providerList);
        System.out.println(msg);
        PrintWriter out = new PrintWriter("target/email.html");
        out.write(msg);
        out.close();
        Assertions.assertTrue(msg.startsWith("<html>"));
        Assertions.assertFalse(msg.contains("notification.email"), "Expected all message keys to have been resolved");
        Assertions.assertTrue(msg.contains("hendelser"));   // TODO norwegian still missing lots of values. How do we verify?

        Assertions.assertTrue(msg.contains(providerList.get(0).getName()), "Should be able to map providerId to name");
    }

    @Test
    public void formatMailWithTooManyEvents() {
        Instant now = Instant.now();

        Notification oldestEvent = maxCrudNotification("NSR:StopPlace:2", now.minusMillis(5000));
        oldestEvent.getEvent().setName("nameShouldNotBeInEmail");

        Set<Notification> notifications = Set.of(oldestEvent, jobNotification(null), maxCrudNotification("NSR:StopPlace:2", now.minusMillis(1000)), maxCrudNotification("NSR:StopPlace:1", now.minusMillis(2000)),
                minCrudNotification("NSR:StopPlace:2", now.minusMillis(3000)), maxCrudNotification("NSR:StopPlace:3", now.minusMillis(4000)));

        String msg = emailNotificationFormatter.formatMessage(notifications, new Locale("en"), providerList);
        System.out.println(msg);
        Assertions.assertTrue(msg.startsWith("<html>"));
        Assertions.assertFalse(msg.contains("notification.email"), "Expected all message keys to have been resolved");

        String messageTooManyEvent =  messageSource.getMessage("notification.email.truncated", new String[] {"6","5"}, Locale.getDefault());
        Assertions.assertTrue(msg.contains(messageTooManyEvent));

        Assertions.assertFalse(msg.contains(oldestEvent.getEvent().getName()), "Expected oldest event to be omitted");
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
        CrudEvent event = CrudEvent.builder().entityType("StopPlace").version(1L).eventTime(time).build();
        event.setPk(pkCounter++);
        notification.setEvent(event);

        return notification;
    }

    private Notification maxCrudNotification(String id, Instant time) {
        Notification notification = new Notification();
        CrudEvent event = CrudEvent.builder().entityType("StopPlace").entityClassifier("onstreetBus").version(1L).comment("comment").changeType("NAME").oldValue("Old name").newValue("Hakkadal").username("UserDTO e").action("CREATE").name("Hakkadal").externalId(id).eventTime(time).build();
        event.setPk(pkCounter++);
        notification.setEvent(event);

        return notification;
    }
}
