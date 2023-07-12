/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.rutebanken.nabu.domain.event;

import org.locationtech.jts.geom.Geometry;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Event representing creation, update or removal of an entity.
 */
@Entity
@DiscriminatorValue("crud")
public class CrudEvent extends Event {

    // Type of entity (ie StopPlace or PlaceOfInterest)
    @NotNull
    @Size(max = 255, message = "entityType cannot be longer than 255 characters")
    private String entityType;

    // Subtype of entity (ie busStop or church)
    @Size(max = 255, message = "entityClassifier cannot be longer than 255 characters")
    private String entityClassifier;

    // What was changed?
    @Size(max = 255, message = "changeType cannot be longer than 255 characters")
    private String changeType;

    // Current version after change
    @NotNull
    private Long version;

    // Value before change (simple update only)
    @Size(max = 255, message = "oldValue cannot be longer than 255 characters")
    private String oldValue;

    // Value after change (simple update only)
    @Size(max = 255, message = "newValue cannot be longer than 255 characters")
    private String newValue;

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "geometry")
    private Geometry geometry;

    @Size(max = 255, message = "comment cannot be longer than 255 characters")
    private String comment;

    @Size(max = 255, message = "location cannot be longer than 255 characters")
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

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
                       ", username='" + getUsername() + '\'' +
                       ", errorCode='" + getErrorCode() + '\'' +
                       ", comment='" + getComment() + '\'' +
                       '}';
    }
}
