package no.rutebanken.nabu.organisation.rest.dto.responsibility;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityClassificationAssignmentDTO {

    public boolean allow = true;

    public String entityClassificationRef;

    public EntityClassificationAssignmentDTO() {
    }

    public EntityClassificationAssignmentDTO(String entityClassificationRef, boolean allow) {
        this.allow = allow;
        this.entityClassificationRef = entityClassificationRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityClassificationAssignmentDTO that = (EntityClassificationAssignmentDTO) o;

        if (allow != that.allow) return false;
        return entityClassificationRef != null ? entityClassificationRef.equals(that.entityClassificationRef) : that.entityClassificationRef == null;
    }

    @Override
    public int hashCode() {
        int result = (allow ? 1 : 0);
        result = 31 * result + (entityClassificationRef != null ? entityClassificationRef.hashCode() : 0);
        return result;
    }
}
