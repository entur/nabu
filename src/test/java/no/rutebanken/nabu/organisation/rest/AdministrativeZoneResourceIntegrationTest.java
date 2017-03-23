package no.rutebanken.nabu.organisation.rest;


import no.rutebanken.nabu.organisation.rest.dto.organisation.AdministrativeZoneDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.wololo.geojson.Polygon;

import java.net.URI;
import java.util.Arrays;

import static no.rutebanken.nabu.organisation.TestConstantsOrganisation.CODE_SPACE_ID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdministrativeZoneResourceIntegrationTest {


	@Autowired
	private TestRestTemplate restTemplate;

	private static final String PATH = "/jersey/administrative_zones";

	@Test
	public void administrativeZoneNotFound() throws Exception {
		ResponseEntity<AdministrativeZoneDTO> entity = restTemplate.getForEntity(PATH + "/unknownAdministrativeZones",
				AdministrativeZoneDTO.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
	}

	@Test
	public void crudAdministrativeZone() throws Exception {
		AdministrativeZoneDTO createAdministrativeZone = createAdministrativeZone("administrativeZone name", "privateCode", validPolygon());
		URI uri = restTemplate.postForLocation(PATH, createAdministrativeZone);
		assertAdministrativeZone(createAdministrativeZone, uri);

		AdministrativeZoneDTO updateAdministrativeZone = createAdministrativeZone("new name", createAdministrativeZone.privateCode, validPolygon());
		restTemplate.put(uri, updateAdministrativeZone);
		assertAdministrativeZone(updateAdministrativeZone, uri);


		AdministrativeZoneDTO[] allAdministrativeZones =
				restTemplate.getForObject(PATH, AdministrativeZoneDTO[].class);
		assertAdministrativeZoneInArray(updateAdministrativeZone, allAdministrativeZones);

		restTemplate.delete(uri);

		ResponseEntity<AdministrativeZoneDTO> entity = restTemplate.getForEntity(uri,
				AdministrativeZoneDTO.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());

	}

	private void assertAdministrativeZoneInArray(AdministrativeZoneDTO administrativeZone, AdministrativeZoneDTO[] array) {
		Assert.assertNotNull(array);
		Assert.assertTrue(Arrays.stream(array).anyMatch(r -> r.privateCode.equals(administrativeZone.privateCode)));
	}

	protected AdministrativeZoneDTO createAdministrativeZone(String name, String privateCode, Polygon polygon) {
		AdministrativeZoneDTO administrativeZone = new AdministrativeZoneDTO();
		administrativeZone.name = name;
		administrativeZone.privateCode = privateCode;
		administrativeZone.polygon = polygon;
		administrativeZone.codeSpace = CODE_SPACE_ID;
		return administrativeZone;
	}

	private Polygon validPolygon() {
		double[][][] coordinates =new double [][][]{{{1.0,1.0},{1.0,2.0},{2.0,2.0},{1.0,1.0}},{{1.0,1.0},{1.0,2.0},{2.0,2.0},{1.0,1.0}}};
		return new Polygon(coordinates);
	}

	protected void assertAdministrativeZone(AdministrativeZoneDTO inAdministrativeZone, URI uri) {
		Assert.assertNotNull(uri);
		ResponseEntity<AdministrativeZoneDTO> rsp = restTemplate.getForEntity(uri, AdministrativeZoneDTO.class);
		AdministrativeZoneDTO outAdministrativeZone = rsp.getBody();
		Assert.assertEquals(inAdministrativeZone.name, outAdministrativeZone.name);
		Assert.assertEquals(inAdministrativeZone.privateCode, outAdministrativeZone.privateCode);
	}

	@Test
	public void createInvalidAdministrativeZone() throws Exception {
		AdministrativeZoneDTO inAdministrativeZone = createAdministrativeZone("administrativeZone name", "privateCode", null);
		ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inAdministrativeZone, String.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatusCode());
	}
}
