package no.rutebanken.nabu.event;

import org.junit.Ignore;
import org.junit.Test;

public class EmailNotificationSenderTest {


    private EmailNotificationSender emailNotificationSender=new EmailNotificationSender();

    @Test
    @Ignore
    public void sendMail() {
        emailNotificationSender.sendEmail("erlend@scelto.no", "testmelding");
    }
}
