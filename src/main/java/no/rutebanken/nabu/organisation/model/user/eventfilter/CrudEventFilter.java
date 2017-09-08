package no.rutebanken.nabu.organisation.model.user.eventfilter;

import no.rutebanken.nabu.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.nabu.organisation.model.responsibility.EntityClassification;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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

    @ManyToMany(fetch = FetchType.EAGER)
    @NotNull
    private Set<EntityClassification> entityClassifications;

    @ManyToMany(fetch = FetchType.EAGER)
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
        getEntityClassifications().clear();
        getEntityClassifications().addAll(entityClassifications);
    }

    @PreRemove
    private void removeConnections() {
        getEntityClassifications().clear();
        getAdministrativeZones().clear();
    }



}
