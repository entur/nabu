package no.rutebanken.nabu.organisation.rest.mapper;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import no.rutebanken.nabu.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.nabu.organisation.repository.CodeSpaceRepository;
import no.rutebanken.nabu.organisation.rest.dto.organisation.AdministrativeZoneDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;

import javax.ws.rs.BadRequestException;

@Service
public class AdministrativeZoneMapper implements DTOMapper<AdministrativeZone, AdministrativeZoneDTO> {

	private GeoJSONWriter writer = new GeoJSONWriter();
	private GeoJSONReader reader = new GeoJSONReader();


	@Autowired
	protected CodeSpaceRepository codeSpaceRepository;


	public AdministrativeZoneDTO toDTO(AdministrativeZone entity, boolean fullDetails) {
		AdministrativeZoneDTO dto = new AdministrativeZoneDTO();
		dto.id = entity.getId();
		dto.name = entity.getName();
		dto.privateCode = entity.getPrivateCode();
		dto.codeSpace = entity.getCodeSpace().getId();
		dto.type = entity.getAdministrativeZoneType();

		if (fullDetails) {
			dto.polygon = (org.wololo.geojson.Polygon) writer.write(entity.getPolygon());
		}
		return dto;
	}

	@Override
	public AdministrativeZone createFromDTO(AdministrativeZoneDTO dto, Class<AdministrativeZone> clazz) {
		AdministrativeZone entity = new AdministrativeZone();
		entity.setPrivateCode(dto.privateCode);
		entity.setCodeSpace(codeSpaceRepository.getOneByPublicId(dto.codeSpace));
		entity.setAdministrativeZoneType(dto.type);
		return updateFromDTO(dto, entity);
	}

	@Override
	public AdministrativeZone updateFromDTO(AdministrativeZoneDTO dto, AdministrativeZone entity) {
		Geometry geometry = reader.read(dto.polygon);
		if (!(geometry instanceof Polygon)) {
			throw new BadRequestException("Polygon is not a valid polygon");
		}
		entity.setPolygon((Polygon) geometry);
		entity.setName(dto.name);
		return entity;
	}
}
