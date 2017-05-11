package no.rutebanken.nabu.organisation.model.user.eventfilter;

import no.rutebanken.nabu.organisation.model.VersionedEntity;
import no.rutebanken.nabu.organisation.model.organisation.Organisation;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public abstract class EventFilter extends VersionedEntity {

    @ManyToOne
    private Organisation organisation;

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

}
