package no.rutebanken.nabu.organization.service;


import no.rutebanken.nabu.organization.repository.CommonRepositoryTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AdministrativeUnitsImporterTest extends CommonRepositoryTest {

	@Autowired
	private AdministrativeUnitsImporter importer;

	@Test
	public void testImport() {
		importer.importAdministrativeUnits(defaultCodeSpace.getId());
	}
}
