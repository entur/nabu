package no.rutebanken.nabu.domain.event;

import com.vividsolutions.jts.geom.Geometry;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Event representing creation, update or removal of an entity.
 */
@Entity
@DiscriminatorValue("crud")
public class CrudEvent extends Event {

    // Type of entity (ie StopPlace or PlaceOfInterest)
    @NotNull
    private String entityType;

    // Sub type of entity (ie busStop or church)
    private String entityClassifier;

    // What was changed?
    private String changeType;

    // Current version after change
    @NotNull
    private Long version;

    // Value before change (simple update only)
    private String oldValue;

    // Value after change (simple update only)
    private String newValue;

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "geometry")
    private Geometry geometry;

    private String comment;

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

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public static CrudEventBuilder builder() {
        return new CrudEventBuilder();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static class CrudEventBuilder extends EventBuilder<CrudEvent> {


        public CrudEventBuilder() {
            super(new CrudEvent());
        }


        public CrudEventBuilder geometry(Geometry geometry) {
            event.geometry = geometry;
            return this;
        }

        public CrudEventBuilder newValue(String newValue) {
            event.newValue = newValue;
            return this;
        }

        public CrudEventBuilder oldValue(String oldValue) {
            event.oldValue = oldValue;
            return this;
        }

        public CrudEventBuilder version(Long version) {
            event.version = version;
            return this;
        }

        public CrudEventBuilder changeType(String changeType) {
            event.changeType = changeType;
            return this;
        }

        public CrudEventBuilder entityType(String entityType) {
            event.entityType = entityType;
            return this;
        }

        public CrudEventBuilder entityClassifier(String entityClassifier) {
            event.entityClassifier = entityClassifier;
            return this;
        }

        public CrudEventBuilder comment(String comment) {
            event.comment = comment;
            return this;
        }
    }

    @Override
    public String toString() {
        return "CrudEvent{" +
                       "entityType='" + entityType + '\'' +
                       ", entityClassifier='" + entityClassifier + '\'' +
                       ", action='" + getAction() + '\'' +
                       ", changeType='" + changeType + '\'' +
                       ", version=" + version +
                       ", oldValue='" + oldValue + '\'' +
                       ", newValue='" + newValue + '\'' +
                       ", eventTime=" + getEventTime() +
                       ", correlationId='" + getCorrelationId() + '\'' +
                       ", name='" + getName() + '\'' +
                       ", externalId='" + getExternalId() + '\'' +
                       ", comment='" + getComment() + '\'' +
                       '}';
    }
}
