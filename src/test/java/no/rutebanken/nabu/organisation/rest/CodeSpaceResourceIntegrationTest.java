package no.rutebanken.nabu.organisation.rest;

import no.rutebanken.nabu.organisation.rest.dto.CodeSpaceDTO;
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
public class CodeSpaceResourceIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	private static final String PATH = "/jersey/code_spaces";

	@Test
	public void codeSpaceNotFound() throws Exception {
		ResponseEntity<CodeSpaceDTO> entity = restTemplate.getForEntity(PATH + "/unknownCodeSpaces",
				CodeSpaceDTO.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
	}

	@Test
	public void crudCodeSpace() throws Exception {
		CodeSpaceDTO createCodeSpace = createCodeSpace("CodeTest", "xmlnsTest", "xmlnsUrlTest");
		URI uri = restTemplate.postForLocation(PATH, createCodeSpace);
		assertCodeSpace(createCodeSpace, uri);

		CodeSpaceDTO updateCodeSpace = createCodeSpace(createCodeSpace.privateCode, createCodeSpace.xmlns,"other url");
		restTemplate.put(uri, updateCodeSpace);
		assertCodeSpace(updateCodeSpace, uri);

		CodeSpaceDTO[] allCodeSpaces =
				restTemplate.getForObject(PATH, CodeSpaceDTO[].class);
		assertCodeSpaceInArray(updateCodeSpace, allCodeSpaces);

		restTemplate.delete(uri);

		ResponseEntity<CodeSpaceDTO> entity = restTemplate.getForEntity(uri,
				CodeSpaceDTO.class);
		Assert.assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());

	}

	private void assertCodeSpaceInArray(CodeSpaceDTO codeSpace, CodeSpaceDTO[] array) {
		Assert.assertNotNull(array);
		Assert.assertTrue(Arrays.stream(array).anyMatch(r -> r.privateCode.equals(codeSpace.privateCode)));
	}

	protected CodeSpaceDTO createCodeSpace(String privateCode, String xmlns, String xmlnsUrl) {
		CodeSpaceDTO codeSpace = new CodeSpaceDTO();
		codeSpace.privateCode = privateCode;
		codeSpace.xmlns = xmlns;
		codeSpace.xmlnsUrl = xmlnsUrl;
		return codeSpace;
	}


	protected void assertCodeSpace(CodeSpaceDTO inCodeSpace, URI uri) {
		Assert.assertNotNull(uri);
		ResponseEntity<CodeSpaceDTO> rsp = restTemplate.getForEntity(uri, CodeSpaceDTO.class);
		CodeSpaceDTO outCodeSpace = rsp.getBody();
		Assert.assertEquals(inCodeSpace.xmlns, outCodeSpace.xmlns);
		Assert.assertEquals(inCodeSpace.xmlnsUrl, outCodeSpace.xmlnsUrl);
		Assert.assertEquals(inCodeSpace.privateCode, outCodeSpace.privateCode);
	}

	@Test
	public void createInvalidCodeSpace() throws Exception {
		CodeSpaceDTO inCodeSpace = createCodeSpace("Code", "xmlns", null);
		ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inCodeSpace, String.class);

		Assert.assertEquals(HttpStatus.BAD_REQUEST, rsp.getStatusCode());
	}

}
