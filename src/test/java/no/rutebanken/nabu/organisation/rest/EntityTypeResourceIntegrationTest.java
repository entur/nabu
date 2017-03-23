package no.rutebanken.nabu.organisation.rest;

import no.rutebanken.nabu.organisation.TestConstantsOrganisation;
import no.rutebanken.nabu.organisation.rest.dto.TypeDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EntityTypeResourceIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	private static final String PATH = "/jersey/entity_types";

	@Test
	public void entityTypeNotFound() throws Exception {
		ResponseEntity<TypeDTO> entity = restTemplate.getForEntity(PATH + "/unknownEntityTypes",
				TypeDTO.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
	}

	@Test
	public void crudEntityType() throws Exception {
		TypeDTO createEntityType = createEntityType("entityType name", "privateCode");
		URI uri = restTemplate.postForLocation(PATH, createEntityType);
		ResourceTestUtils.assertType(createEntityType, uri, restTemplate);

		TypeDTO updateEntityType = createEntityType("new name", createEntityType.privateCode);
		restTemplate.put(uri, updateEntityType);
		ResourceTestUtils.assertType(updateEntityType, uri, restTemplate);


		TypeDTO[] allEntityTypes =
				restTemplate.getForObject(PATH, TypeDTO[].class);
		assertEntityTypeInArray(updateEntityType, allEntityTypes);

		restTemplate.delete(uri);

		ResponseEntity<TypeDTO> entity = restTemplate.getForEntity(uri,
				TypeDTO.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());

	}

	private void assertEntityTypeInArray(TypeDTO entityType, TypeDTO[] array) {
		Assert.assertNotNull(array);
		Assert.assertTrue(Arrays.stream(array).anyMatch(r -> r.privateCode.equals(entityType.privateCode)));
	}

	protected TypeDTO createEntityType(String name, String privateCode) {
		TypeDTO entityType = new TypeDTO();
		entityType.name = name;
		entityType.privateCode = privateCode;
		entityType.codeSpace = TestConstantsOrganisation.CODE_SPACE_ID;
		return entityType;
	}


	@Test
	public void createInvalidEntityType() throws Exception {
		TypeDTO inEntityType = createEntityType("entityType name", null);
		ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inEntityType, String.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatusCode());
	}

}