package no.rutebanken.nabu.organization.rest;


import no.rutebanken.nabu.organization.model.responsibility.EntityClassification;
import no.rutebanken.nabu.organization.model.responsibility.EntityType;
import no.rutebanken.nabu.organization.repository.EntityClassificationRepository;
import no.rutebanken.nabu.organization.repository.EntityTypeRepository;
import no.rutebanken.nabu.organization.rest.dto.TypeDTO;
import no.rutebanken.nabu.organization.rest.mapper.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/entity_types/{entityTypeId}/entity_classifications")
@Produces("application/json")
@Transactional
public class EntityClassificationResource {

	@Autowired
	private EntityClassificationRepository repository;

	@Autowired
	private EntityTypeRepository entityTypeRepository;

	@Autowired
	private TypeMapper<EntityClassification> mapper;


	@POST
	public Response create(@PathParam("entityTypeId") String entityTypeId, TypeDTO dto, @Context UriInfo uriInfo) {
		EntityType entityType = getEntityType(entityTypeId);
		dto.codeSpace = entityType.getCodeSpace().getId();
		EntityClassification entity = mapper.createFromDTO(dto, EntityClassification.class);
		entity.setEntityType(entityType);
		entity = repository.save(entity);
		return buildCreatedResponse(uriInfo, entity);
	}


	@PUT
	@Path("{id}")
	public void update(@PathParam("entityTypeId") String entityTypeId, @PathParam("id") String id, TypeDTO dto) {
		EntityClassification entity = getExisting(id, entityTypeId);

		repository.save(mapper.updateFromDTO(dto, entity));
	}


	@GET
	@Path("{id}")
	public TypeDTO get(@PathParam("entityTypeId") String entityTypeId, @PathParam("id") String id) {
		EntityClassification entity = getExisting(id, entityTypeId);
		return mapper.toDTO(entity, true);
	}

	@DELETE
	@Path("{id}")
	public void delete(@PathParam("entityTypeId") String entityTypeId, @PathParam("id") String id) {
		repository.delete(getExisting(id, entityTypeId));
	}

	@GET
	public List<TypeDTO> listAll(@PathParam("entityTypeId") String entityTypeId) {
		EntityType entityType = getEntityType(entityTypeId);
		if (CollectionUtils.isEmpty(entityType.getClassifications())) {
			return new ArrayList<>();
		}
		return entityType.getClassifications().stream().map(r -> mapper.toDTO(r, false)).collect(Collectors.toList());
	}


	protected Response buildCreatedResponse(UriInfo uriInfo, EntityClassification entity) {
		UriBuilder builder = uriInfo.getAbsolutePathBuilder();
		builder.path(entity.getId());
		return Response.created(builder.build()).build();
	}

	protected EntityClassification getExisting(String id, String entityTypeId) {
		try {
			EntityClassification entity = repository.getOneByPublicId(id);
			if (!entity.getEntityType().getId().equals(entityTypeId)) {
				throw new NotFoundException("EntityClassification with id: [" + id + "] not found for entity type with id: " + entityTypeId);
			}
			return entity;
		} catch (DataRetrievalFailureException e) {
			throw new NotFoundException("EntityClassification with id: [" + id + "] not found");
		}
	}

	protected EntityType getEntityType(String id) {
		try {
			return entityTypeRepository.getOneByPublicId(id);
		} catch (DataRetrievalFailureException e) {
			throw new NotFoundException("EntityType with id: [" + id + "] not found");
		}
	}
}