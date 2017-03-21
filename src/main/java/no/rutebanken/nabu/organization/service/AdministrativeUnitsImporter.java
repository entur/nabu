package no.rutebanken.nabu.organization.service;

import com.vividsolutions.jts.geom.Polygon;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import no.rutebanken.nabu.organization.model.CodeSpace;
import no.rutebanken.nabu.organization.model.Id;
import no.rutebanken.nabu.organization.model.organization.AdministrativeZone;
import no.rutebanken.nabu.organization.repository.AdministrativeZoneRepository;
import no.rutebanken.nabu.organization.repository.CodeSpaceRepository;
import org.apache.commons.io.FileUtils;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AdministrativeUnitsImporter {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AdministrativeZoneRepository repository;

	@Autowired
	private CodeSpaceRepository codeSpaceRepository;

	private static final String tmpFile = "files/tmp.geojson";

	public void importAdministrativeUnits(String codeSpaceId) {
		importAdministrativeUnits("files/fylker.geojson", "fylkesnr", codeSpaceId);
		importAdministrativeUnits("files/kommuner.geojson", "komm", codeSpaceId);
	}

	public void importAdministrativeUnits(String filePath, String idProperty, String codeSpaceId) {

		new FeatureJSONFilter(filePath, tmpFile, idProperty, "area").filter();


		CodeSpace codeSpace = codeSpaceRepository.getOneByPublicId(codeSpaceId);
		List<AdministrativeZone> administrativeZones = new ArrayList<>();

		try {
			FeatureJSON fJson = new FeatureJSON();
			FeatureIterator<SimpleFeature> itr = fJson.streamFeatureCollection(FileUtils.openInputStream(new File(tmpFile)));

			while (itr.hasNext()) {
				SimpleFeature feature = itr.next();

				AdministrativeZone administrativeZone = new AdministrativeZone();

				administrativeZone.setCodeSpace(codeSpace);
				administrativeZone.setName(getProperty(feature, "navn"));
				administrativeZone.setPolygon((Polygon) feature.getDefaultGeometry());

				String id;
				String type = getProperty(feature, "objtype");
				if ("Fylke".equals(type)) {
					id = StringUtils.leftPad("" + getProperty(feature, "fylkesnr"), 2, "0");
				} else if ("Kommune".equals(type)) {
					id = StringUtils.leftPad("" + getProperty(feature, "komm"), 4, "0");
				} else {
					logger.info("Ignoring unknown feature: " + type);
					continue;
				}

				administrativeZone.setPrivateCode(id);
				administrativeZones.add(administrativeZone);
			}
			itr.close();
			repository.save(administrativeZones);

			new File(tmpFile).delete();
		} catch (Exception e) {
			throw new RuntimeException("Import of admin units failed with exception: " + e.getMessage(), e);
		}
	}

	public <T> T getProperty(Feature feature, String propertyName) {
		Property property = feature.getProperty(propertyName);
		if (property == null) {
			return null;
		}
		return (T) property.getValue();
	}
}