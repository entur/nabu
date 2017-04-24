package no.rutebanken.nabu.organisation.rest.dto.user;

import no.rutebanken.nabu.organisation.rest.dto.BaseDTO;
import no.rutebanken.nabu.organisation.rest.dto.organisation.OrganisationDTO;
import no.rutebanken.nabu.organisation.rest.dto.responsibility.ResponsibilitySetDTO;

import java.util.ArrayList;
import java.util.List;


public class UserDTO extends BaseDTO {

    public String username;

    public String organisationRef;

    public List<String> responsibilitySetRefs = new ArrayList<>();

    public ContactDetailsDTO contactDetails;

    public List<NotificationDTO> notifications = new ArrayList<>();


    // Full objects included for ease of use, disregarded in CRUD
    public OrganisationDTO organisation;

    public List<ResponsibilitySetDTO> responsibilitySets;
}
