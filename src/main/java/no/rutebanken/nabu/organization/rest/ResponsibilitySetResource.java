package no.rutebanken.nabu.organization.rest;

import no.rutebanken.nabu.organization.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organization.model.user.User;
import no.rutebanken.nabu.organization.repository.ResponsibilitySetRepository;
import no.rutebanken.nabu.organization.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organization.rest.dto.responsibility.ResponsibilitySetDTO;
import no.rutebanken.nabu.organization.rest.dto.user.UserDTO;
import no.rutebanken.nabu.organization.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organization.rest.mapper.ResponsibilitySetMapper;
import no.rutebanken.nabu.organization.service.IamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/responsibility_sets")
@Produces("application/json")
@Transactional
public class ResponsibilitySetResource extends BaseResource<ResponsibilitySet, ResponsibilitySetDTO> {

	@Autowired
	private ResponsibilitySetRepository repository;

	@Autowired
	private ResponsibilitySetMapper mapper;

	@Autowired
	private IamService iamService;

	@Override
	protected Class<ResponsibilitySet> getEntityClass() {
		return ResponsibilitySet.class;
	}

	@Override
	protected VersionedEntityRepository<ResponsibilitySet> getRepository() {
		return repository;
	}

	@Override
	protected DTOMapper<ResponsibilitySet, ResponsibilitySetDTO> getMapper() {
		return mapper;
	}


	@PUT
	@Path("{id}")
	public void update(@PathParam("id") String id, ResponsibilitySetDTO dto) {
		ResponsibilitySet entity = repository.getOneByPublicId(id);
		repository.save(mapper.updateFromDTO(dto, entity));
		iamService.updateResponsibilitySet(entity);
	}


}
