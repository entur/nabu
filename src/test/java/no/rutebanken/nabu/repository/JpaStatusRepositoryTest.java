package no.rutebanken.nabu.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import no.rutebanken.nabu.domain.Status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaStatusRepositoryTest {

	@Autowired
	StatusRepository repository;

	@Test
	public void testUpdate() throws Exception {
		Status input = new Status("00013-gtfs.zip", 2L, 1L, Status.Action.IMPORT, Status.State.OK, "1234567", new Date(),"ost");
		repository.add(input);
	}

	@Test
	public void testGetStatusForProvider() throws Exception {
		Collection<Status> statuses = repository.getStatusForProvider(2L);
		assertThat(statuses).hasSize(3);
	}

}