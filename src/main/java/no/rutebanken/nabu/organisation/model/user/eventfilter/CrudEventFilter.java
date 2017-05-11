package no.rutebanken.nabu.organisation.model.user.eventfilter;

import no.rutebanken.nabu.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.nabu.organisation.model.responsibility.EntityClassification;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import java.util.HashSet;
import java.util.Set;

@Entity
public class CrudEventFilter extends EventFilter {

    @ManyToOne
    private EntityClassification entityClassification;

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

    public EntityClassification getEntityClassification() {
        return entityClassification;
    }

    public void setEntityClassification(EntityClassification entityClassification) {
        this.entityClassification = entityClassification;
    }

    @PreRemove
    private void removeAdministrativeZoneConnections() {
        getAdministrativeZones().clear();
    }

}
