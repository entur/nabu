package no.rutebanken.nabu.organisation.model.responsibility;

import no.rutebanken.nabu.organisation.model.CodeSpaceEntity;
import no.rutebanken.nabu.organisation.model.TypeEntity;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {
                                   @UniqueConstraint(name = "entity_type_unique_id", columnNames = {"code_space_pk", "privateCode", "entityVersion"})
})
public class EntityType extends CodeSpaceEntity implements TypeEntity {

    @NotNull
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return "TypeOfEntity";
    }

    @OneToMany(mappedBy = "entityType", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntityClassification> classifications;

    public Set<EntityClassification> getClassifications() {
        if (classifications == null) {
            this.classifications = new HashSet<>();
        }
        return classifications;
    }

    public void setClassifications(Set<EntityClassification> classifications) {
        getClassifications().clear();
        getClassifications().addAll(classifications);
    }

    public EntityClassification getClassification(String privateCode) {
        if (privateCode != null && !CollectionUtils.isEmpty(classifications)) {
            for (EntityClassification existingClassification : classifications) {
                if (privateCode.equals(existingClassification.getPrivateCode())) {
                    return existingClassification;
                }
            }
        }
        return null;
    }

}
