package no.rutebanken.nabu.event.user.dto.user;


import no.rutebanken.nabu.event.user.dto.BaseDTO;
import no.rutebanken.nabu.event.user.dto.organisation.OrganisationDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class UserDTO extends BaseDTO {

    public String username;

    public String organisationRef;

    public List<String> responsibilitySetRefs = new ArrayList<>();

    public ContactDetailsDTO contactDetails;

    public Set<NotificationConfigDTO> notifications = new HashSet<>();


    // Full objects included for ease of use, disregarded in CRUD
    public OrganisationDTO organisation;

    public String getUsername() {
        return username;
    }

    public String getOrganisationRef() {
        return organisationRef;
    }

    public List<String> getResponsibilitySetRefs() {
        return responsibilitySetRefs;
    }

    public ContactDetailsDTO getContactDetails() {
        return contactDetails;
    }

    public Set<NotificationConfigDTO> getNotifications() {
        return notifications;
    }

    public OrganisationDTO getOrganisation() {
        return organisation;
    }
}
