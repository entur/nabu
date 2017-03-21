package no.rutebanken.nabu.organization.rest.mapper;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import no.rutebanken.nabu.organization.model.organization.AdministrativeZone;
import no.rutebanken.nabu.organization.rest.dto.organisation.AdministrativeZoneDTO;
import org.springframework.stereotype.Service;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;

import javax.ws.rs.BadRequestException;

@Service
public class AdministrativeZoneMapper extends BaseDTOMapper<AdministrativeZone, AdministrativeZoneDTO> {

	private GeoJSONWriter writer = new GeoJSONWriter();
	private GeoJSONReader reader = new GeoJSONReader();

	public AdministrativeZoneDTO toDTO(AdministrativeZone entity) {
		AdministrativeZoneDTO dto = toDTOBasics(entity, new AdministrativeZoneDTO());
		dto.name = entity.getName();

		dto.polygon = (org.wololo.geojson.Polygon) writer.write(entity.getPolygon());
		return dto;
	}

	@Override
	public AdministrativeZone createFromDTO(AdministrativeZoneDTO dto, Class<AdministrativeZone> clazz) {
		return updateFromDTO(dto, new AdministrativeZone());
	}

	@Override
	public AdministrativeZone updateFromDTO(AdministrativeZoneDTO dto, AdministrativeZone entity) {
		fromDTOBasics(entity, dto);

		Geometry geometry = reader.read(dto.polygon);
		if (!(geometry instanceof Polygon)) {
			throw new BadRequestException("Polygon is not a valid polygon");
		}
		entity.setPolygon((Polygon) geometry);
		entity.setName(dto.name);
		return entity;
	}
}
