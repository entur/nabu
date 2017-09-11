package no.rutebanken.nabu.event.user.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.event.user.dto.organisation.OrganisationDTO;
import no.rutebanken.nabu.event.user.dto.responsibility.EntityClassificationDTO;

import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventFilterDTO {

    public enum EventFilterType {JOB, CRUD}


    public EventFilterType type;

    public String organisationRef;

    // Full objects included for ease of use, disregarded in CRUD
    public OrganisationDTO organisation;

    // TODO components/subclasses?

    // Job event filter values
    public String jobDomain;

    public Set<String> actions = new HashSet<>();

    public Set<JobState> states = new HashSet<>();

    // Crud event filter values
    public Set<String> administrativeZoneRefs = new HashSet<>();

    public Set<String> entityClassificationRefs = new HashSet<>();

    // Full objects included for ease of use, disregarded in CRUD
    public Set<EntityClassificationDTO> entityClassifications = new HashSet<>();

    public EventFilterDTO(EventFilterType type) {
        this.type = type;
    }

    public EventFilterDTO() {
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventFilterDTO that = (EventFilterDTO) o;

        if (type != that.type) return false;
        if (organisationRef != null ? !organisationRef.equals(that.organisationRef) : that.organisationRef != null)
            return false;
        if (jobDomain != that.jobDomain) return false;
        if (actions != null ? !actions.equals(that.actions) : that.actions != null) return false;
        if (states != null ? !states.equals(that.states) : that.states != null) return false;
        if (administrativeZoneRefs != null ? !administrativeZoneRefs.equals(that.administrativeZoneRefs) : that.administrativeZoneRefs != null)
            return false;
        return entityClassificationRefs != null ? entityClassificationRefs.equals(that.entityClassificationRefs) : that.entityClassificationRefs == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (organisationRef != null ? organisationRef.hashCode() : 0);
        result = 31 * result + (jobDomain != null ? jobDomain.hashCode() : 0);
        result = 31 * result + (actions != null ? actions.hashCode() : 0);
        result = 31 * result + (states != null ? states.hashCode() : 0);
        result = 31 * result + (administrativeZoneRefs != null ? administrativeZoneRefs.hashCode() : 0);
        result = 31 * result + (entityClassificationRefs != null ? entityClassificationRefs.hashCode() : 0);
        return result;
    }

    public EventFilterType getType() {
        return type;
    }

    public String getOrganisationRef() {
        return organisationRef;
    }

    public OrganisationDTO getOrganisation() {
        return organisation;
    }

    public String getJobDomain() {
        return jobDomain;
    }

    public Set<String> getActions() {
        return actions;
    }

    public Set<JobState> getStates() {
        return states;
    }

    public Set<String> getAdministrativeZoneRefs() {
        return administrativeZoneRefs;
    }

    public Set<String> getEntityClassificationRefs() {
        return entityClassificationRefs;
    }

    public Set<EntityClassificationDTO> getEntityClassifications() {
        return entityClassifications;
    }
}
