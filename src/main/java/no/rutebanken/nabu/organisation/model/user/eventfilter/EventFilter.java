package no.rutebanken.nabu.organisation.model.user.eventfilter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.rutebanken.nabu.event.filter.EventMatcher;
import no.rutebanken.nabu.organisation.model.organisation.Organisation;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;

/**
 * User defined filter for events.
 */
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class EventFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long pk;

    @ManyToOne
    private Organisation organisation;

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }


    /**
     * Get matcher for this event filter.
     * <p>
     * (NB! This couples Organisation model with event model needs to be revised if applications are separated)
     */
    public abstract EventMatcher getMatcher();
}
