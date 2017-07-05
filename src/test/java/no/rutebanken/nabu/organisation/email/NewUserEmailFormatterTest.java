package no.rutebanken.nabu.organisation.email;

import no.rutebanken.nabu.NabuTestApp;
import no.rutebanken.nabu.organisation.model.user.ContactDetails;
import no.rutebanken.nabu.organisation.model.user.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NabuTestApp.class)
public class NewUserEmailFormatterTest {
    @Autowired
    private NewUserEmailFormatter emailFormatter;

    @Test
    public void testFormatNewUserEmail() {
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setEmail("e@e.org");
        contactDetails.setFirstName("First");
        contactDetails.setLastName("Last");
        User user = User.builder().withContactDetails(contactDetails).withUsername("test-user").build();

        String msg = emailFormatter.formatMessage(user,  new Locale("no"));

        Assert.assertTrue(msg.startsWith("<html>"));
        Assert.assertTrue(msg.contains(contactDetails.getFirstName() + " " + contactDetails.getLastName()));
        Assert.assertTrue(msg.contains(user.getUsername()));

        System.out.println(msg);
    }

    @Test
    public void testGetNewUserEmailSubject() {
        Assert.assertEquals("Entur - New account details", emailFormatter.getSubject(Locale.ENGLISH));
    }

}
