package no.rutebanken.nabu.organisation.rest.validation;

import no.rutebanken.nabu.organisation.model.VersionedEntity;
import no.rutebanken.nabu.organisation.repository.ResponsibilitySetRepository;
import no.rutebanken.nabu.organisation.rest.dto.TypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class TypeValidator<E extends VersionedEntity> implements DTOValidator<E, TypeDTO> {

    @Autowired
    private ResponsibilitySetRepository responsibilitySetRepository;

    @Override
    public void validateCreate(TypeDTO dto) {
        Assert.hasLength(dto.name, "name required");
        Assert.hasLength(dto.privateCode, "privateCode required");
    }

    @Override
    public void validateUpdate(TypeDTO dto, E entity) {
        Assert.hasLength(dto.name, "name required");
    }

    @Override
    public void validateDelete(E entity) {
        Assert.isTrue(responsibilitySetRepository.getResponsibilitySetsReferringTo(entity).isEmpty(),"referred to by responsibilitySet");
    }
}
