package no.rutebanken.nabu.organisation.rest;

import no.rutebanken.nabu.organisation.TestConstantsOrganisation;
import no.rutebanken.nabu.organisation.repository.BaseIntegrationTest;
import no.rutebanken.nabu.organisation.rest.dto.organisation.OrganisationDTO;
import no.rutebanken.nabu.organisation.rest.dto.organisation.OrganisationPartDTO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

public class OrganisationResourceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String PATH = "/jersey/organisations";

    @Test
    public void organisationNotFound() throws Exception {
        ResponseEntity<OrganisationDTO> entity = restTemplate.getForEntity(PATH + "/unknownOrganisation",
                OrganisationDTO.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    public void crudOrganisation() throws Exception {

        OrganisationDTO createOrganisation = createOrganisation("TheOrg", "Org name", null);
        URI uri = restTemplate.postForLocation(PATH, createOrganisation);
        assertOrganisation(createOrganisation, uri);

        OrganisationPartDTO orgPart1 = new OrganisationPartDTO();
        orgPart1.name = "part 1";

        OrganisationDTO updateOrganisation = createOrganisation(createOrganisation.privateCode, "newOrg name", 2l, orgPart1);
        restTemplate.put(uri, updateOrganisation);
        assertOrganisation(updateOrganisation, uri);

        OrganisationDTO[] allOrganisations =
                restTemplate.getForObject(PATH, OrganisationDTO[].class);
        assertOrganisationInArray(updateOrganisation, allOrganisations);

        restTemplate.delete(uri);

        ResponseEntity<OrganisationDTO> entity = restTemplate.getForEntity(uri,
                OrganisationDTO.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());

    }

    @Test
    public void updateOrganisationParts() throws Exception {
        OrganisationPartDTO orgPart1 = new OrganisationPartDTO();
        orgPart1.name = "part 1";
        orgPart1.administrativeZoneRefs = ResourceTestUtils.addAdminZones(restTemplate, "amd1", "adm2");

        OrganisationPartDTO orgPart2 = new OrganisationPartDTO();
        orgPart2.name = "part2";

        OrganisationDTO organisation = createOrganisation("OrgWithParts", "Org name", null, orgPart1, orgPart2);
        URI uri = restTemplate.postForLocation(PATH, organisation);
        assertOrganisation(organisation, uri);

        orgPart1.administrativeZoneRefs.remove(0);
        orgPart1.administrativeZoneRefs.addAll(ResourceTestUtils.addAdminZones(restTemplate, "adm3"));

        restTemplate.put(uri, organisation);
        assertOrganisation(organisation, uri);
        organisation.parts.remove(orgPart2);

        OrganisationPartDTO orgPart3 = new OrganisationPartDTO();
        orgPart3.name = "part3";
        organisation.parts.add(orgPart3);

        restTemplate.put(uri, organisation);
        assertOrganisation(organisation, uri);

        organisation.parts = null;
        restTemplate.put(uri, organisation);
        assertOrganisation(organisation, uri);
    }


    private void assertOrganisationInArray(OrganisationDTO organisation, OrganisationDTO[] array) {
        Assert.assertNotNull(array);
        Assert.assertTrue(Arrays.stream(array).anyMatch(r -> r.privateCode.equals(organisation.privateCode)));
    }

    protected OrganisationDTO createOrganisation(String privateCode, String name, Long companyNumber, OrganisationPartDTO... parts) {
        OrganisationDTO organisation = new OrganisationDTO();
        organisation.organisationType = OrganisationDTO.OrganisationType.AUTHORITY;
        organisation.codeSpace = TestConstantsOrganisation.CODE_SPACE_ID;
        organisation.privateCode = privateCode;
        organisation.name = name;
        organisation.companyNumber = companyNumber;
        if (parts != null) {
            organisation.parts = new ArrayList<>(Arrays.asList(parts));
        }

        return organisation;
    }


    protected void assertOrganisation(OrganisationDTO inOrganisation, URI uri) {
        Assert.assertNotNull(uri);
        ResponseEntity<OrganisationDTO> rsp = restTemplate.getForEntity(uri, OrganisationDTO.class);
        OrganisationDTO outOrganisation = rsp.getBody();
        Assert.assertEquals(inOrganisation.name, outOrganisation.name);
        Assert.assertEquals(inOrganisation.privateCode, outOrganisation.privateCode);
        Assert.assertEquals(inOrganisation.companyNumber, outOrganisation.companyNumber);

        if (CollectionUtils.isEmpty(inOrganisation.parts)) {
            Assert.assertTrue(CollectionUtils.isEmpty(outOrganisation.parts));
        } else {
            Assert.assertEquals(inOrganisation.parts.size(), outOrganisation.parts.size());
            for (OrganisationPartDTO in : inOrganisation.parts) {
                Assert.assertTrue(outOrganisation.parts.stream().anyMatch(out -> isEqual(in, out)));
            }
        }

    }

    private boolean isEqual(OrganisationPartDTO in, OrganisationPartDTO out) {
        if (!in.name.equals(out.name)) {
            return false;
        }

        if (CollectionUtils.isEmpty(in.administrativeZoneRefs)) {
            return CollectionUtils.isEmpty(out.administrativeZoneRefs);
        }

        if (in.administrativeZoneRefs.size() != out.administrativeZoneRefs.size()) {
            return false;
        }
        return in.administrativeZoneRefs.containsAll(out.administrativeZoneRefs);
    }

    @Test
    public void createInvalidOrganisation() throws Exception {
        OrganisationPartDTO partWithoutName = new OrganisationPartDTO();
        OrganisationDTO inOrganisation = createOrganisation("privateCode", "organisation name", null, partWithoutName);
        ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inOrganisation, String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatusCode());
    }

    @Test
    public void createOrgWithExistingPrivateCode() throws Exception {
        OrganisationDTO inOrganisation = createOrganisation("OrgPrivateCode", "organisation name", null);
        ResponseEntity<String> firstRsp = restTemplate.postForEntity(PATH, inOrganisation, String.class);

        Assert.assertEquals(HttpStatus.CREATED, firstRsp.getStatusCode());

        ResponseEntity<String> secondRsp = restTemplate.postForEntity(PATH, inOrganisation, String.class);
        Assert.assertEquals(HttpStatus.CONFLICT, secondRsp.getStatusCode());
    }

}
