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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.Arrays;

public class RoleResourceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String PATH = "/jersey/roles";

    @Test
    public void roleNotFound() throws Exception {
        ResponseEntity<TypeDTO> entity = restTemplate.getForEntity(PATH + "/unknownRoles",
                TypeDTO.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    public void crudRole() throws Exception {
        TypeDTO createRole = createRole("role name", "privateCode");
        URI uri = restTemplate.postForLocation(PATH, createRole);
        ResourceTestUtils.assertType(createRole, uri, restTemplate);

        TypeDTO updateRole = createRole("new name", createRole.privateCode);
        restTemplate.put(uri, updateRole);
        ResourceTestUtils.assertType(updateRole, uri, restTemplate);


        TypeDTO[] allRoles =
                restTemplate.getForObject(PATH, TypeDTO[].class);
        assertRoleInArray(updateRole, allRoles);

        restTemplate.delete(uri);

        ResponseEntity<TypeDTO> entity = restTemplate.getForEntity(uri,
                TypeDTO.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    public void testDeleteRoleInUse() {
        ResponseEntity<String> response = restTemplate.exchange(PATH + "/" + TestConstantsOrganisation.ROLE_ID, HttpMethod.DELETE, null, String.class);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    private void assertRoleInArray(TypeDTO role, TypeDTO[] array) {
        Assert.assertNotNull(array);
        Assert.assertTrue(Arrays.stream(array).anyMatch(r -> r.privateCode.equals(role.privateCode)));
    }

    protected TypeDTO createRole(String name, String privateCode) {
        TypeDTO role = new TypeDTO();
        role.name = name;
        role.privateCode = privateCode;
        return role;
    }

    @Test
    public void createInvalidRole() throws Exception {
        TypeDTO inRole = createRole("role name", null);
        ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inRole, String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatusCode());
    }

}
