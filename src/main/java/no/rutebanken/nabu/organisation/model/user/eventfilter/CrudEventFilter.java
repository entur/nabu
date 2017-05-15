package no.rutebanken.nabu.organisation.model.user.eventfilter;

import no.rutebanken.nabu.event.filter.CrudEventMatcher;
import no.rutebanken.nabu.event.filter.EventMatcher;
import no.rutebanken.nabu.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.nabu.organisation.model.responsibility.EntityClassification;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * User defined filter for Crud/changelog events.
 */
@Entity
public class CrudEventFilter extends EventFilter {

    @ManyToMany
    @NotNull
    private Set<EntityClassification> entityClassifications;

    @ManyToMany
    private Set<AdministrativeZone> administrativeZones;

    public Set<AdministrativeZone> getAdministrativeZones() {
        if (administrativeZones == null) {
            administrativeZones = new HashSet<>();
        }
        return administrativeZones;
    }

    public void setAdministrativeZones(Set<AdministrativeZone> administrativeZones) {
        getAdministrativeZones().clear();
        getAdministrativeZones().addAll(administrativeZones);

    }

    public Set<EntityClassification> getEntityClassifications() {
        if (entityClassifications == null) {
            entityClassifications = new HashSet<>();
        }
        return entityClassifications;
    }

    public void setEntityClassifications(Set<EntityClassification> entityClassifications) {
        this.entityClassifications = entityClassifications;
    }

    @Override
    public EventMatcher getMatcher() {
        return new CrudEventMatcher(this);
    }

    @PreRemove
    private void removeConnections() {
        getEntityClassifications().clear();
        getAdministrativeZones().clear();
    }



}
