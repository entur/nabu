package no.rutebanken.nabu.organisation.rest;


import no.rutebanken.nabu.organisation.model.organisation.Organisation;
import no.rutebanken.nabu.organisation.repository.OrganisationRepository;
import no.rutebanken.nabu.organisation.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organisation.rest.dto.organisation.OrganisationDTO;
import no.rutebanken.nabu.organisation.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organisation.rest.mapper.OrganisationMapper;
import no.rutebanken.nabu.organisation.rest.validation.DTOValidator;
import no.rutebanken.nabu.organisation.rest.validation.OrganisationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;

@Component
@Path("/organisations")
@Produces("application/json")
@Transactional
@PreAuthorize("hasRole('" + ROLE_ORGANISATION_EDIT + "')")
public class OrganisationResource extends AnnotatedBaseResource<Organisation, OrganisationDTO> {


	@Autowired
	private OrganisationRepository repository;
	@Autowired
	private OrganisationMapper mapper;
	@Autowired
	private OrganisationValidator validator;

	@Override
	protected Class<Organisation> getEntityClass() {
		return Organisation.class;
	}

	@Override
	protected VersionedEntityRepository<Organisation> getRepository() {
		return repository;
	}

	@Override
	protected DTOMapper<Organisation, OrganisationDTO> getMapper() {
		return mapper;
	}

	@Override
	protected DTOValidator<Organisation, OrganisationDTO> getValidator() {
		return validator;
	}
}
