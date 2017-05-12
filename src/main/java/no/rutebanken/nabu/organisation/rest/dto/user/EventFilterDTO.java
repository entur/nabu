package no.rutebanken.nabu.organisation.rest.dto.user;

import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.organisation.rest.dto.organisation.OrganisationDTO;

import java.util.ArrayList;
import java.util.List;

public class EventFilterDTO {

    public enum EventFilterType {JOB, CRUD}


    public EventFilterType type;

    public String organisationRef;

    // Full objects included for ease of use, disregarded in CRUD
    public OrganisationDTO organisation;

    // TODO components/subclasses?

    // Job event filter values
    public JobEvent.JobDomain jobDomain;

    public String action;

    public JobState state;

    // Crud event filter values
    public List<String> administrativeZoneRefs = new ArrayList<>();

    public List<String> entityClassificationRefs = new ArrayList<>();


    public EventFilterDTO(EventFilterType type) {
        this.type = type;
    }

    public EventFilterDTO() {
    }
}
