package no.rutebanken.nabu.repository;


import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.domain.SystemStatus;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaSystemStatusRepositoryTest {

	@Autowired
	SystemStatusRepository repository;

	private Date now = new Date();

	@Test
	public void testAdd() throws Exception {
		SystemStatus input = new SystemStatus("4444444", SystemStatus.Action.BUILD_GRAPH, SystemStatus.State.OK, "entity", ",source", "target", now);
		repository.add(input);
	}


	@Test
	public void testGetSystemStatusWithAllCriteria() throws Exception {
		SystemStatus s1 = new SystemStatus("1234567", SystemStatus.Action.BUILD_GRAPH, SystemStatus.State.OK, "entity1", "source1", "target1", now);
		repository.add(s1);
		SystemStatus s2 = new SystemStatus("1234567", SystemStatus.Action.BUILD_GRAPH, SystemStatus.State.OK, "entity1", "source1", "target1", DateUtils.addMinutes(now, 1));
		repository.add(s2);
		SystemStatus s3 = new SystemStatus("12345678", SystemStatus.Action.BUILD_GRAPH, SystemStatus.State.OK, "entity2", "source2", "target2", now);
		repository.add(s3);

		Collection<SystemStatus> statusesQueryMatchingS1 = repository.getSystemStatus(now, now, Arrays.asList(SystemStatus.Action.BUILD_GRAPH), Arrays.asList(SystemStatus.State.OK), Arrays.asList("entity1"),
				Arrays.asList("source1"), Arrays.asList("target1"));
		assertThat(statusesQueryMatchingS1).hasSize(2);
		assertThat(statusesQueryMatchingS1).contains(s1, s2);


		Collection<SystemStatus> statusesQueryMatchingS1andS3 = repository.getSystemStatus(now, now, Arrays.asList(SystemStatus.Action.BUILD_GRAPH, SystemStatus.Action.EXPORT),
				Arrays.asList(SystemStatus.State.OK, SystemStatus.State.TIMEOUT), Arrays.asList("entity1", "entity2"), null, null);
		assertThat(statusesQueryMatchingS1andS3).hasSize(3);
	}

}
