package no.rutebanken.nabu.organization.repository;

import no.rutebanken.nabu.App;
import no.rutebanken.nabu.organization.model.CodeSpace;
import no.rutebanken.nabu.organization.model.organization.Authority;
import no.rutebanken.nabu.organization.model.organization.Organisation;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
@Transactional
public abstract class CommonRepositoryTest {

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

}
