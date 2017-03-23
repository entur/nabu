package no.rutebanken.nabu.organisation.rest;

import no.rutebanken.nabu.organisation.rest.dto.TypeDTO;
import no.rutebanken.nabu.organisation.rest.dto.organisation.AdministrativeZoneDTO;
import org.junit.Assert;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.wololo.geojson.Polygon;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static no.rutebanken.nabu.organisation.TestConstantsOrganisation.CODE_SPACE_ID;

public class ResourceTestUtils {

	public static List<String> addAdminZones(TestRestTemplate restTemplate, String... privateCodes) {
		List<String> ids = new ArrayList<>();
		for (String privateCode : privateCodes) {
			ids.add(addAdminZone(restTemplate, privateCode));
		}

		return ids;
	}

	public static String addAdminZone(TestRestTemplate restTemplate, String privateCode) {
		AdministrativeZoneDTO updateAdministrativeZone = createAdministrativeZone(privateCode, privateCode, validPolygon());
		URI uri = restTemplate.postForLocation("/jersey/administrative_zones", updateAdministrativeZone);
		return new File(uri.getPath()).getName();
	}

	public static void assertType(TypeDTO in, URI uri, TestRestTemplate restTemplate) {
		Assert.assertNotNull(uri);
		ResponseEntity<TypeDTO> rsp = restTemplate.getForEntity(uri, TypeDTO.class);
		TypeDTO out = rsp.getBody();
		Assert.assertEquals(in.name, out.name);
		Assert.assertEquals(in.privateCode, out.privateCode);
	}

	public static AdministrativeZoneDTO createAdministrativeZone(String name, String privateCode, Polygon polygon) {
		AdministrativeZoneDTO administrativeZone = new AdministrativeZoneDTO();
		administrativeZone.name = name;
		administrativeZone.privateCode = privateCode;
		administrativeZone.polygon = polygon;
		administrativeZone.codeSpace = CODE_SPACE_ID;
		return administrativeZone;
	}

	public static Polygon validPolygon() {
		double[][][] coordinates = new double[][][]{{{1.0, 1.0}, {1.0, 2.0}, {2.0, 2.0}, {1.0, 1.0}}, {{1.0, 1.0}, {1.0, 2.0}, {2.0, 2.0}, {1.0, 1.0}}};
		return new Polygon(coordinates);
	}

}
