package no.rutebanken.nabu.organisation.rest.mapper;

import no.rutebanken.nabu.organisation.model.responsibility.EntityClassification;
import no.rutebanken.nabu.organisation.model.responsibility.EntityType;
import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilityRoleAssignment;
import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organisation.repository.CodeSpaceRepository;
import no.rutebanken.nabu.organisation.rest.dto.TypeDTO;
import no.rutebanken.nabu.organisation.rest.dto.responsibility.EntityTypeDTO;
import no.rutebanken.nabu.organisation.rest.dto.responsibility.ResponsibilityRoleAssignmentDTO;
import no.rutebanken.nabu.organisation.rest.dto.responsibility.ResponsibilitySetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EntityTypeMapper implements DTOMapper<EntityType, EntityTypeDTO> {

    @Autowired
    private TypeMapper<EntityClassification> classificationTypeMapper;

    @Autowired
    protected CodeSpaceRepository codeSpaceRepository;


    @Override
    public EntityType createFromDTO(EntityTypeDTO dto, Class clazz) {
        EntityType entity = new EntityType();
        entity.setPrivateCode(dto.privateCode);
        entity.setCodeSpace(codeSpaceRepository.getOneByPublicId(dto.codeSpace));

        return updateFromDTO(dto, entity);
    }

    @Override
    public EntityType updateFromDTO(EntityTypeDTO dto, EntityType entity) {
        entity.setName(dto.name);

        if (CollectionUtils.isEmpty(dto.classifications)) {
            entity.setClassifications(new HashSet<>());
        } else {
            mergeClassifications(dto, entity);
        }

        return entity;
    }

    @Override
    public EntityTypeDTO toDTO(EntityType entity, boolean fullDetails) {
        EntityTypeDTO dto = new EntityTypeDTO();
        dto.name = entity.getName();
        dto.id = entity.getId();
        dto.privateCode = entity.getPrivateCode();
        dto.codeSpace = entity.getCodeSpace().getId();

        dto.classifications = entity.getClassifications().stream().map(ec -> classificationTypeMapper.toDTO(ec, false)).collect(Collectors.toList());
        return dto;
    }

    protected void mergeClassifications(EntityTypeDTO dto, EntityType entity) {
        Set<EntityClassification> removedClassifications = new HashSet<>(entity.getClassifications());

        for (TypeDTO dtoClassification : dto.classifications) {
            EntityClassification existingClassification = entity.getClassification(dtoClassification.privateCode);

            if (existingClassification != null) {
                removedClassifications.remove(existingClassification);
                classificationTypeMapper.updateFromDTO(dtoClassification, existingClassification);
            } else {
                dtoClassification.codeSpace = entity.getCodeSpace().getId();
                EntityClassification newClassification = classificationTypeMapper.createFromDTO(dtoClassification, EntityClassification.class);
                newClassification.setEntityType(entity);
                entity.getClassifications().add(newClassification);
            }
        }
        entity.getClassifications().removeAll(removedClassifications);
    }

}
