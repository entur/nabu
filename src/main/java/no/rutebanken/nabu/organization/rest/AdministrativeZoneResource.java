package no.rutebanken.nabu.organization.rest;

import no.rutebanken.nabu.organization.model.organization.AdministrativeZone;
import no.rutebanken.nabu.organization.repository.AdministrativeZoneRepository;
import no.rutebanken.nabu.organization.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organization.rest.dto.organisation.AdministrativeZoneDTO;
import no.rutebanken.nabu.organization.rest.mapper.AdministrativeZoneMapper;
import no.rutebanken.nabu.organization.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organization.service.AdministrativeUnitsImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/administrative_zones")
@Produces("application/json")
@Transactional
public class AdministrativeZoneResource extends BaseResource<AdministrativeZone, AdministrativeZoneDTO> {


	@Autowired
	private AdministrativeZoneRepository repository;

	@Autowired
	private AdministrativeUnitsImporter importer;
	@Autowired
	private AdministrativeZoneMapper mapper;

	@POST
	@Path("/import/{codeSpaceId}")
	public void importFromFile(@PathParam("codeSpaceId") String codeSpaceId) {
		importer.importAdministrativeUnits(codeSpaceId);
	}

	@Override
	protected VersionedEntityRepository<AdministrativeZone> getRepository() {
		return repository;
	}

	@Override
	protected DTOMapper<AdministrativeZone, AdministrativeZoneDTO> getMapper() {
		return mapper;
	}

	@Override
	protected Class<AdministrativeZone> getEntityClass() {
		return AdministrativeZone.class;
	}
}
