package no.rutebanken.nabu.organisation.rest;

import no.rutebanken.nabu.organisation.TestConstantsOrganisation;
import no.rutebanken.nabu.organisation.repository.BaseIntegrationTest;
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


public class EntityClassificationResourceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String PATH = "/jersey/entity_types/" + TestConstantsOrganisation.ENTITY_TYPE_ID + "/entity_classifications";

    @Test
    public void entityClassificationNotFound() throws Exception {
        ResponseEntity<TypeDTO> entity = restTemplate.getForEntity(PATH + "/unknownEntityClassifications",
                TypeDTO.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    public void crudEntityClassification() throws Exception {
        TypeDTO createEntityClassification = createEntityClassification("entityClassification name", "privateCode");
        URI uri = restTemplate.postForLocation(PATH, createEntityClassification);
        ResourceTestUtils.assertType(createEntityClassification, uri, restTemplate);

        TypeDTO updateEntityClassification = createEntityClassification("new name", createEntityClassification.privateCode);
        restTemplate.put(uri, updateEntityClassification);
        ResourceTestUtils.assertType(updateEntityClassification, uri, restTemplate);


        TypeDTO[] allEntityClassifications =
                restTemplate.getForObject(PATH, TypeDTO[].class);
        assertEntityClassificationInArray(updateEntityClassification, allEntityClassifications);

        restTemplate.delete(uri);

        ResponseEntity<TypeDTO> entity = restTemplate.getForEntity(uri,
                TypeDTO.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());

    }

    private void assertEntityClassificationInArray(TypeDTO entityClassification, TypeDTO[] array) {
        Assert.assertNotNull(array);
        Assert.assertTrue(Arrays.stream(array).anyMatch(r -> r.privateCode.equals(entityClassification.privateCode)));
    }

    protected TypeDTO createEntityClassification(String name, String privateCode) {
        TypeDTO entityClassification = new TypeDTO();
        entityClassification.name = name;
        entityClassification.privateCode = privateCode;
        entityClassification.codeSpace = TestConstantsOrganisation.CODE_SPACE_ID;
        return entityClassification;
    }

    @Test
    public void createInvalidEntityClassification() throws Exception {
        TypeDTO inEntityClassification = createEntityClassification("entityClassification name", null);
        ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inEntityClassification, String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatusCode());
    }
}