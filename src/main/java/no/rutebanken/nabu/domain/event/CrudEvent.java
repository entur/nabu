package no.rutebanken.nabu.domain.event;

import com.vividsolutions.jts.geom.Geometry;

import javax.persistence.*;

/**
 * Event representing creation, update or removal of an entity.
 */
@Entity
@DiscriminatorValue("crud")
public class CrudEvent extends Event {

    private String entityType;

    private String entityClassifier;

    private Long version;

    private String oldValue;

    private String newValue;

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "geometry")
    private Geometry geometry;

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityClassifier() {
        return entityClassifier;
    }

    public void setEntityClassifier(String entityClassifier) {
        this.entityClassifier = entityClassifier;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
