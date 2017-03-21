package no.rutebanken.nabu.organization.rest;


import no.rutebanken.nabu.organization.model.responsibility.EntityClassification;
import no.rutebanken.nabu.organization.repository.EntityClassificationRepository;
import no.rutebanken.nabu.organization.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organization.rest.dto.TypeDTO;
import no.rutebanken.nabu.organization.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organization.rest.mapper.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/entity_classifications")
@Produces("application/json")
@Transactional
public class EntityClassificationResource extends BaseResource<EntityClassification, TypeDTO> {

	@Autowired
	private EntityClassificationRepository repository;

	@Autowired
	private TypeMapper<EntityClassification> mapper;

	@Override
	protected Class<EntityClassification> getEntityClass() {
		return EntityClassification.class;
	}

	@Override
	protected VersionedEntityRepository<EntityClassification> getRepository() {
		return repository;
	}

	@Override
	protected DTOMapper<EntityClassification, TypeDTO> getMapper() {
		return mapper;
	}
}