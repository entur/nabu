package no.rutebanken.nabu.organisation.repository;

import org.junit.Assert;
import org.junit.Test;

public class CodeSpaceRepositoryTest extends CommonRepositoryTest {


	@Test
	public void testFindByPublicId() {
		Assert.assertNotNull(codeSpaceRepository.getOneByPublicId(defaultCodeSpace.getId()));
	}

}
