package no.rutebanken.nabu.organisation.rest.dto.user;

import no.rutebanken.nabu.organisation.rest.dto.BaseDTO;
import no.rutebanken.nabu.organisation.rest.dto.organisation.OrganisationDTO;
import no.rutebanken.nabu.organisation.rest.dto.responsibility.ResponsibilitySetDTO;

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

    public List<ResponsibilitySetDTO> responsibilitySets;
}
