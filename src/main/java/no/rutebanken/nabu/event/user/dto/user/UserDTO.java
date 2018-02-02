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
