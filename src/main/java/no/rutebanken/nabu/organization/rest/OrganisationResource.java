package no.rutebanken.nabu.organization.rest;


import no.rutebanken.nabu.organization.model.organization.Organisation;
import no.rutebanken.nabu.organization.repository.OrganisationRepository;
import no.rutebanken.nabu.organization.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organization.rest.dto.organisation.OrganisationDTO;
import no.rutebanken.nabu.organization.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organization.rest.mapper.OrganisationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Component
@Path("/organisations")
@Produces("application/json")
@Transactional
public class OrganisationResource extends BaseResource<Organisation, OrganisationDTO> {


	@Autowired
	private OrganisationRepository repository;
	@Autowired
	private OrganisationMapper mapper;

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
}
