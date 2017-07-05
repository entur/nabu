package no.rutebanken.nabu.organisation.model.organisation;

import com.google.common.base.Joiner;
import com.vividsolutions.jts.geom.Polygon;
import no.rutebanken.nabu.organisation.model.CodeSpaceEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(uniqueConstraints = {
                                   @UniqueConstraint(name = "adm_zone_unique_id", columnNames = {"code_space_pk", "privateCode", "entityVersion"})
})
public class AdministrativeZone extends CodeSpaceEntity {

    private static final String ROLE_ASSIGNMENT_TYPE_NAME = "TopographicPlace";

    @NotNull
    private String name;

    @NotNull
    private String source;

    /**
     * Polygon is wrapped in PersistablePolygon.
     * Because we want to fetch polygons lazily and using lazy property fetching with byte code enhancement breaks tests.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private PersistablePolygon polygon;


    @NotNull
    @Enumerated(EnumType.STRING)
    private AdministrativeZoneType administrativeZoneType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Polygon getPolygon() {
        if (polygon == null) {
            return null;
        }
        return polygon.getPolygon();
    }

    public void setPolygon(Polygon polygon) {
        if (polygon == null) {
            this.polygon = null;
        } else {
            this.polygon = new PersistablePolygon(polygon);
        }
    }


    public AdministrativeZoneType getAdministrativeZoneType() {
        return administrativeZoneType;
    }

    public void setAdministrativeZoneType(AdministrativeZoneType administrativeZoneType) {
        this.administrativeZoneType = administrativeZoneType;
    }

    /**
     * Return ID for this admin zone when referred to in a role assignment.
     *
     */
    @Transient
    public String getRoleAssignmentId() {
        return Joiner.on(":").join(getSource(), ROLE_ASSIGNMENT_TYPE_NAME, getPrivateCode());
    }

}
