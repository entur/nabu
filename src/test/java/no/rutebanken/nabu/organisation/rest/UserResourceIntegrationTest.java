package no.rutebanken.nabu.organisation.rest;

import no.rutebanken.nabu.organisation.TestConstantsOrganisation;
import no.rutebanken.nabu.organisation.repository.BaseIntegrationTest;
import no.rutebanken.nabu.organisation.rest.dto.user.ContactDetailsDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.NotificationDTO;
import no.rutebanken.nabu.organisation.rest.dto.user.UserDTO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.Arrays;


public class UserResourceIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private static final String PATH = "/jersey/users";


    @Test
    public void userNotFound() throws Exception {
        ResponseEntity<UserDTO> entity = restTemplate.getForEntity(PATH + "/unknownUser",
                UserDTO.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());

    }


    @Test
    public void crudUser() throws Exception {
        ContactDetailsDTO createContactDetails = new ContactDetailsDTO("first", "last", "email", "phone");
        UserDTO createUser = createUser("userName", TestConstantsOrganisation.ORGANISATION_ID, createContactDetails);
        createUser.notifications = Arrays.asList(new NotificationDTO(NotificationDTO.NotificationType.EMAIL, "change1"),
                new NotificationDTO(NotificationDTO.NotificationType.EMAIL, "change2"));
        URI uri = restTemplate.postForLocation(PATH, createUser);
        assertUser(createUser, uri);

        ContactDetailsDTO updateContactDetails = new ContactDetailsDTO("otherFirst", "otherLast", "otherEmail", null);
        UserDTO updateUser = createUser(createUser.username, createUser.organisationRef, updateContactDetails);
        restTemplate.put(uri, updateUser);
        assertUser(updateUser, uri);

        UserDTO[] allUsers =
                restTemplate.getForObject(PATH, UserDTO[].class);
        assertUserInArray(updateUser, allUsers);

        restTemplate.delete(uri);

        ResponseEntity<UserDTO> entity = restTemplate.getForEntity(uri,
                UserDTO.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    public void updateUsersResponsibilitySets() throws Exception {
        UserDTO user = createUser("userName", TestConstantsOrganisation.ORGANISATION_ID, null);
        URI uri = restTemplate.postForLocation(PATH, user);
        assertUser(user, uri);

        user.responsibilitySetRefs = Arrays.asList(TestConstantsOrganisation.RESPONSIBILITY_SET_ID);
        restTemplate.put(uri, user);
        assertUser(user, uri);

        user.responsibilitySetRefs = Arrays.asList(TestConstantsOrganisation.RESPONSIBILITY_SET_ID, TestConstantsOrganisation.RESPONSIBILITY_SET_ID_2);
        restTemplate.put(uri, user);
        assertUser(user, uri);

        user.responsibilitySetRefs = Arrays.asList(TestConstantsOrganisation.RESPONSIBILITY_SET_ID_2);
        restTemplate.put(uri, user);
        assertUser(user, uri);

        user.responsibilitySetRefs = null;
        restTemplate.put(uri, user);
        assertUser(user, uri);
    }

    private void assertUserInArray(UserDTO user, UserDTO[] array) {
        Assert.assertNotNull(array);
        Assert.assertTrue(Arrays.stream(array).anyMatch(r -> r.username.equals(user.username.toLowerCase())));
    }

    protected UserDTO createUser(String username, String orgRef, ContactDetailsDTO contactDetails, String... respSetRefs) {
        UserDTO user = new UserDTO();
        user.username = username;
        user.organisationRef = orgRef;
        user.contactDetails = contactDetails;
        if (respSetRefs != null) {
            user.responsibilitySetRefs = Arrays.asList(respSetRefs);
        }

        return user;
    }


    protected void assertUser(UserDTO inUser, URI uri) {
        Assert.assertNotNull(uri);
        ResponseEntity<UserDTO> rsp = restTemplate.getForEntity(uri, UserDTO.class);
        UserDTO outUser = rsp.getBody();
        Assert.assertEquals(inUser.username.toLowerCase(), outUser.username);
        Assert.assertEquals(inUser.privateCode, outUser.privateCode);

        if (CollectionUtils.isEmpty(inUser.responsibilitySetRefs)) {
            Assert.assertTrue(CollectionUtils.isEmpty(outUser.responsibilitySetRefs));
        } else {
            Assert.assertEquals(inUser.responsibilitySetRefs.size(), outUser.responsibilitySetRefs.size());
            Assert.assertTrue(inUser.responsibilitySetRefs.containsAll(outUser.responsibilitySetRefs));
        }


        if (inUser.contactDetails == null) {
            Assert.assertNull(outUser.contactDetails);
        } else {
            Assert.assertEquals(inUser.contactDetails.firstName, outUser.contactDetails.firstName);
            Assert.assertEquals(inUser.contactDetails.lastName, outUser.contactDetails.lastName);
            Assert.assertEquals(inUser.contactDetails.email, outUser.contactDetails.email);
            Assert.assertEquals(inUser.contactDetails.phone, outUser.contactDetails.phone);
        }

        if (CollectionUtils.isEmpty(inUser.notifications)) {
            Assert.assertTrue(CollectionUtils.isEmpty(outUser.notifications));
        } else {
            Assert.assertEquals(inUser.notifications.size(), outUser.notifications.size());
            for (NotificationDTO in : inUser.notifications) {
                Assert.assertTrue(outUser.notifications.stream().anyMatch(out -> isEqual(in, out)));
            }
        }

    }

    private boolean isEqual(NotificationDTO in, NotificationDTO out) {
        return in.notificationType == out.notificationType && in.trigger.equals(out.trigger);
    }

    @Test
    public void createInvalidUser() throws Exception {
        UserDTO inUser = createUser("user name", "privateCode", null);
        ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inUser, String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatusCode());
    }

}
