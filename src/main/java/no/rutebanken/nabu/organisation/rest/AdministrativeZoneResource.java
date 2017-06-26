package no.rutebanken.nabu.organisation.rest;

import no.rutebanken.nabu.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.nabu.organisation.model.organisation.AdministrativeZoneType;
import no.rutebanken.nabu.organisation.model.user.NotificationType;
import no.rutebanken.nabu.organisation.repository.AdministrativeZoneRepository;
import no.rutebanken.nabu.organisation.repository.VersionedEntityRepository;
import no.rutebanken.nabu.organisation.rest.dto.organisation.AdministrativeZoneDTO;
import no.rutebanken.nabu.organisation.rest.mapper.AdministrativeZoneMapper;
import no.rutebanken.nabu.organisation.rest.mapper.DTOMapper;
import no.rutebanken.nabu.organisation.rest.validation.AdministrativeZoneValidator;
import no.rutebanken.nabu.organisation.rest.validation.DTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ORGANISATION_EDIT;

@Component
@Path("/administrative_zones")
@Produces("application/json")
@Transactional
@PreAuthorize("hasRole('" + ROLE_ORGANISATION_EDIT + "')")
public class AdministrativeZoneResource extends AnnotatedBaseResource<AdministrativeZone, AdministrativeZoneDTO> {


    @Autowired
    private AdministrativeZoneRepository repository;
    @Autowired
    private AdministrativeZoneMapper mapper;
    @Autowired
    private AdministrativeZoneValidator validator;

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


    @GET
    @Path("types")
    public AdministrativeZoneType[] getAdministrativeZoneTypes() {
        return AdministrativeZoneType.values();
    }

}
