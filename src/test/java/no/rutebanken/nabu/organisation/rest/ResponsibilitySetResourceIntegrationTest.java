package no.rutebanken.nabu.organisation.rest;

import no.rutebanken.nabu.organisation.TestConstantsOrganisation;
import no.rutebanken.nabu.organisation.repository.BaseIntegrationTest;
import no.rutebanken.nabu.organisation.rest.dto.responsibility.ResponsibilityRoleAssignmentDTO;
import no.rutebanken.nabu.organisation.rest.dto.responsibility.ResponsibilitySetDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;



public class ResponsibilitySetResourceIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	private static final String PATH = "/jersey/responsibility_sets";

	@Test
	public void responsibilitySetNotFound() throws Exception {
		ResponseEntity<ResponsibilitySetDTO> entity = restTemplate.getForEntity(PATH + "/unknownResponsibilitySet",
				ResponsibilitySetDTO.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
	}

	@Test
	public void crudResponsibilitySet() throws Exception {

		ResponsibilitySetDTO createResponsibilitySet = createResponsibilitySet("RspSet", "RspSet name",
				new ResponsibilityRoleAssignmentDTO(TestConstantsOrganisation.ROLE_ID, TestConstantsOrganisation.ORGANISATION_ID));

		URI uri = restTemplate.postForLocation(PATH, createResponsibilitySet);
		assertResponsibilitySet(createResponsibilitySet, uri);

		ResponsibilityRoleAssignmentDTO role1 = new ResponsibilityRoleAssignmentDTO(TestConstantsOrganisation.ROLE_ID, TestConstantsOrganisation.ORGANISATION_ID);

		ResponsibilitySetDTO updateResponsibilitySet = createResponsibilitySet(createResponsibilitySet.privateCode, "RspSet new name", role1);
		restTemplate.put(uri, updateResponsibilitySet);
		assertResponsibilitySet(updateResponsibilitySet, uri);

		ResponsibilitySetDTO[] allResponsibilitySets =
				restTemplate.getForObject(PATH, ResponsibilitySetDTO[].class);
		assertResponsibilitySetInArray(updateResponsibilitySet, allResponsibilitySets);

		restTemplate.delete(uri);

		ResponseEntity<ResponsibilitySetDTO> entity = restTemplate.getForEntity(uri,
				ResponsibilitySetDTO.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());

	}

	@Test
	public void updateResponsibilitySetRoles() throws Exception {
		ResponsibilityRoleAssignmentDTO role1 = new ResponsibilityRoleAssignmentDTO(TestConstantsOrganisation.ROLE_ID, TestConstantsOrganisation.ORGANISATION_ID);
		role1.responsibleOrganisationRef = TestConstantsOrganisation.ORGANISATION_ID;
		role1.typeOfResponsibilityRoleRef = TestConstantsOrganisation.ROLE_ID;
		role1.responsibleAreaRef = ResourceTestUtils.addAdminZone(restTemplate, "respArea1");

		ResponsibilityRoleAssignmentDTO role2 = new ResponsibilityRoleAssignmentDTO(TestConstantsOrganisation.ROLE_ID, TestConstantsOrganisation.ORGANISATION_ID);
		role2.entityClassificationRefs = Arrays.asList(TestConstantsOrganisation.ENTITY_CLASSIFICATION_ID);

		ResponsibilitySetDTO responsibilitySet = createResponsibilitySet("RspSetUpdate", "RspSet name", role1, role2);
		URI uri = restTemplate.postForLocation(PATH, responsibilitySet);
		assertResponsibilitySet(responsibilitySet, uri);

		role1.responsibleAreaRef = null;
		role2.entityClassificationRefs = Arrays.asList(TestConstantsOrganisation.ENTITY_CLASSIFICATION_ID, TestConstantsOrganisation.ENTITY_CLASSIFICATION_ID_2);

		restTemplate.put(uri, responsibilitySet);
		assertResponsibilitySet(responsibilitySet, uri);
		responsibilitySet.roles.remove(role2);

		ResponsibilityRoleAssignmentDTO role3 = new ResponsibilityRoleAssignmentDTO(TestConstantsOrganisation.ROLE_ID, TestConstantsOrganisation.ORGANISATION_ID);
		responsibilitySet.roles.add(role3);
		role1.entityClassificationRefs = Arrays.asList(TestConstantsOrganisation.ENTITY_CLASSIFICATION_ID_2);
		role2.entityClassificationRefs = null;

		restTemplate.put(uri, responsibilitySet);
		assertResponsibilitySet(responsibilitySet, uri);
	}


	private void assertResponsibilitySetInArray(ResponsibilitySetDTO responsibilitySet, ResponsibilitySetDTO[] array) {
		Assert.assertNotNull(array);
		Assert.assertTrue(Arrays.stream(array).anyMatch(r -> r.privateCode.equals(responsibilitySet.privateCode)));
	}

	protected ResponsibilitySetDTO createResponsibilitySet(String privateCode, String name, ResponsibilityRoleAssignmentDTO... roles) {
		ResponsibilitySetDTO responsibilitySet = new ResponsibilitySetDTO();
		responsibilitySet.codeSpace = TestConstantsOrganisation.CODE_SPACE_ID;
		responsibilitySet.privateCode = privateCode;
		responsibilitySet.name = name;
		if (roles != null) {
			responsibilitySet.roles = new ArrayList<>(Arrays.asList(roles));
		}

		return responsibilitySet;
	}


	protected void assertResponsibilitySet(ResponsibilitySetDTO inResponsibilitySet, URI uri) {
		Assert.assertNotNull(uri);
		ResponseEntity<ResponsibilitySetDTO> rsp = restTemplate.getForEntity(uri, ResponsibilitySetDTO.class);
		ResponsibilitySetDTO outResponsibilitySet = rsp.getBody();
		Assert.assertEquals(inResponsibilitySet.name, outResponsibilitySet.name);
		Assert.assertEquals(inResponsibilitySet.privateCode, outResponsibilitySet.privateCode);
		Assert.assertEquals(inResponsibilitySet.codeSpace, outResponsibilitySet.codeSpace);

		if (CollectionUtils.isEmpty(inResponsibilitySet.roles)) {
			Assert.assertTrue(CollectionUtils.isEmpty(outResponsibilitySet.roles));
		} else {
			Assert.assertEquals(inResponsibilitySet.roles.size(), outResponsibilitySet.roles.size());
			for (ResponsibilityRoleAssignmentDTO in : inResponsibilitySet.roles) {
				Assert.assertTrue(outResponsibilitySet.roles.stream().anyMatch(out -> isEqual(in, out)));
			}
		}

	}

	private boolean isEqual(ResponsibilityRoleAssignmentDTO in, ResponsibilityRoleAssignmentDTO out) {
		if (!in.typeOfResponsibilityRoleRef.equals(out.typeOfResponsibilityRoleRef)) {
			return false;
		}
		if (!in.responsibleOrganisationRef.equals(out.responsibleOrganisationRef)) {
			return false;
		}
		if (in.responsibleAreaRef == null) {
			if (out.responsibleAreaRef != null) {
				return false;
			}
		} else if (!in.responsibleAreaRef.equals(out.responsibleAreaRef)) {
			return false;
		}

		if (CollectionUtils.isEmpty(in.entityClassificationRefs)) {
			return CollectionUtils.isEmpty(out.entityClassificationRefs);
		} else if (out.entityClassificationRefs == null) {
			return false;
		}

		if (in.entityClassificationRefs.size() != out.entityClassificationRefs.size()) {
			return false;
		}
		return in.entityClassificationRefs.containsAll(out.entityClassificationRefs);
	}

	@Test
	public void createInvalidResponsibilitySetWithMissingRoles() throws Exception {
		ResponsibilityRoleAssignmentDTO roleWithoutName = new ResponsibilityRoleAssignmentDTO();
		ResponsibilitySetDTO inResponsibilitySet = createResponsibilitySet("privateCode", "responsibilitySet name", roleWithoutName);
		ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inResponsibilitySet, String.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatusCode());
	}

	@Test
	public void createOrgWithExistingPrivateCode() throws Exception {
		ResponsibilitySetDTO inResponsibilitySet = createResponsibilitySet("OrgPrivateCode", "responsibilitySet name",
				new ResponsibilityRoleAssignmentDTO(TestConstantsOrganisation.ROLE_ID, TestConstantsOrganisation.ORGANISATION_ID));

		ResponseEntity<String> firstRsp = restTemplate.postForEntity(PATH, inResponsibilitySet, String.class);

		Assert.assertEquals(HttpStatus.CREATED, firstRsp.getStatusCode());

		ResponseEntity<String> secondRsp = restTemplate.postForEntity(PATH, inResponsibilitySet, String.class);
		Assert.assertEquals(HttpStatus.CONFLICT, secondRsp.getStatusCode());
	}
}