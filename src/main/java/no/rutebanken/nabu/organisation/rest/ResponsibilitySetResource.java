package no.rutebanken.nabu.organisation.rest;

import io.swagger.annotations.Api;
import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.nabu.organisation.repository.ResponsibilitySetRepository;
import no.rutebanken.nabu.organisation.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organisation.rest.dto.responsibility.ResponsibilitySetDTO;
import no.rutebanken.nabu.organisation.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organisation.rest.mapper.ResponsibilitySetMapper;
import no.rutebanken.nabu.organisation.rest.validation.DTOValidator;
import no.rutebanken.nabu.organisation.rest.validation.ResponsibilitySetValidator;
import no.rutebanken.nabu.organisation.service.IamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;

@Component
@Path("/responsibility_sets")
@Produces("application/json")
@Transactional
@PreAuthorize("hasRole('" + ROLE_ORGANISATION_EDIT + "')")
@Api
public class ResponsibilitySetResource extends AnnotatedBaseResource<ResponsibilitySet, ResponsibilitySetDTO> {

	@Autowired
	private ResponsibilitySetRepository repository;

	@Autowired
	private ResponsibilitySetMapper mapper;

	@Autowired
	private ResponsibilitySetValidator validator;

	@Autowired
	private IamService iamService;


	@PUT
	@Path("{id}")
	public void update(@PathParam("id") String id, ResponsibilitySetDTO dto) {
		ResponsibilitySet entity = updateEntity(id,dto);
		iamService.updateResponsibilitySet(entity);
	}


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

	@Override
	protected DTOValidator<ResponsibilitySet, ResponsibilitySetDTO> getValidator() {
		return validator;
	}

}
