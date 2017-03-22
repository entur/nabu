package no.rutebanken.nabu.organisation.rest;

import no.rutebanken.nabu.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.nabu.organisation.repository.AdministrativeZoneRepository;
import no.rutebanken.nabu.organisation.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organisation.rest.dto.organisation.AdministrativeZoneDTO;
import no.rutebanken.nabu.organisation.rest.mapper.AdministrativeZoneMapper;
import no.rutebanken.nabu.organisation.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organisation.rest.validation.AdministrativeZoneValidator;
import no.rutebanken.nabu.organisation.rest.validation.DTOValidator;
import no.rutebanken.nabu.organisation.service.AdministrativeUnitsImporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.InputStream;

@Component
@Path("/administrative_zones")
@Produces("application/json")
@Transactional
public class AdministrativeZoneResource extends AnnotatedBaseResource<AdministrativeZone, AdministrativeZoneDTO> {


	@Autowired
	private AdministrativeZoneRepository repository;

	@Autowired
	private AdministrativeUnitsImporter importer;
	@Autowired
	private AdministrativeZoneMapper mapper;
	@Autowired
	private AdministrativeZoneValidator validator;

	@POST
	@Path("/import/{codeSpaceId}")
	public void importFromFile(@PathParam("codeSpaceId") String codeSpaceId) {
		importer.importAdministrativeUnits(codeSpaceId);
	}

	@POST
	@Path("/import/{codeSpaceId}/kommuner")
	public void importKommuner(@PathParam("codeSpaceId") String codeSpaceId, InputStream inputStream) {
		importer.importKommuner(inputStream, codeSpaceId);
	}

	@POST
	@Path("/import/{codeSpaceId}/fylker")
	public void importFylker(@PathParam("codeSpaceId") String codeSpaceId, InputStream inputStream) {
		importer.importFylker(inputStream, codeSpaceId);
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

	@Override
	protected DTOValidator<AdministrativeZone, AdministrativeZoneDTO> getValidator() {
		return validator;
	}
}
