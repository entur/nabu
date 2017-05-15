package no.rutebanken.nabu.organisation.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.rutebanken.nabu.NabuTestApp;
import no.rutebanken.nabu.organisation.model.CodeSpace;
import no.rutebanken.nabu.organisation.model.organisation.Authority;
import no.rutebanken.nabu.organisation.model.organisation.Organisation;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = NabuTestApp.class)
@Transactional
public abstract class BaseIntegrationTest {

	@Autowired
	protected CodeSpaceRepository codeSpaceRepository;

	@Autowired
	protected OrganisationRepository organisationRepository;


	protected Organisation defaultOrganisation;

	protected CodeSpace defaultCodeSpace;

	@Before
	public void setUp() {
		CodeSpace codeSpace = new CodeSpace("nsr", "NSR", "http://www.rutebanken.org/ns/nsr");
		defaultCodeSpace = codeSpaceRepository.saveAndFlush(codeSpace);

		Authority authority = new Authority();
		authority.setCodeSpace(defaultCodeSpace);
		authority.setName("Test Org");
		authority.setPrivateCode("testOrg");
		defaultOrganisation = organisationRepository.saveAndFlush(authority);
	}


	protected String toJson(Object o){
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			StringWriter writer = new StringWriter();
			mapper.writeValue(writer, o);
			return writer.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
