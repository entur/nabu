package no.rutebanken.nabu.organisation.model.responsibility;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class EntityClassificationAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long pk;

    @ManyToOne
    @NotNull
    private EntityClassification entityClassification;

    @ManyToOne
    private ResponsibilityRoleAssignment responsibilityRoleAssignment;

    private boolean allow = true;

    public EntityClassificationAssignment() {
    }

    public EntityClassificationAssignment(EntityClassification entityClassification, ResponsibilityRoleAssignment responsibilityRoleAssignment, boolean allow) {
        this.entityClassification = entityClassification;
        this.responsibilityRoleAssignment = responsibilityRoleAssignment;
        this.allow = allow;
    }

    public EntityClassification getEntityClassification() {
        return entityClassification;
    }

    public void setEntityClassification(EntityClassification entityClassification) {
        this.entityClassification = entityClassification;
    }

    public boolean isAllow() {
        return allow;
    }

    public void setAllow(boolean allow) {
        this.allow = allow;
    }


    public ResponsibilityRoleAssignment getResponsibilityRoleAssignment() {
        return responsibilityRoleAssignment;
    }

    public void setResponsibilityRoleAssignment(ResponsibilityRoleAssignment responsibilityRoleAssignment) {
        this.responsibilityRoleAssignment = responsibilityRoleAssignment;
    }

    @Override
    public String toString() {
        return "EntityClassificationAssignment{" +
                       "entityClassification=" + entityClassification +
                       ", allow=" + allow +
                       '}';
    }
}
