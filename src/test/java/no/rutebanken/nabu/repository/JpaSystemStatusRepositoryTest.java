package no.rutebanken.nabu.repository;


import no.rutebanken.nabu.domain.SystemStatus;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JpaSystemStatusRepositoryTest {

	@Autowired
	SystemStatusRepository repository;

	private Date now = new Date();

	@Test
	public void testAdd() throws Exception {
		SystemStatus input = new SystemStatus("job", "4444444", SystemStatus.Action.BUILD, SystemStatus.State.OK, "entity", ",source", "target", now);
		repository.add(input);
	}


	@Test
	public void testGetSystemStatusWithAllCriteria() throws Exception {
		SystemStatus s1 = new SystemStatus("job1", "1234567", SystemStatus.Action.BUILD, SystemStatus.State.OK, "entity1", "source1", "target1", now);
		repository.add(s1);
		SystemStatus s2 = new SystemStatus("job1", "1234567", SystemStatus.Action.BUILD, SystemStatus.State.OK, "entity1", "source1", "target1", DateUtils.addMinutes(now, 1));
		repository.add(s2);
		SystemStatus s3 = new SystemStatus("job1", "12345678", SystemStatus.Action.BUILD, SystemStatus.State.OK, "entity2", "source2", "target2", now);
		repository.add(s3);

		Collection<SystemStatus> statusesQueryMatchingS1 = repository.getSystemStatus(now, now, Arrays.asList("job1"), Arrays.asList(SystemStatus.Action.BUILD), Arrays.asList(SystemStatus.State.OK), Arrays.asList("entity1"),
				Arrays.asList("source1"), Arrays.asList("target1"));
		assertThat(statusesQueryMatchingS1).hasSize(2);
		assertThat(statusesQueryMatchingS1).contains(s1, s2);


		Collection<SystemStatus> statusesQueryMatchingS1andS3 = repository.getSystemStatus(now, now, null, Arrays.asList(SystemStatus.Action.BUILD, SystemStatus.Action.EXPORT),
				Arrays.asList(SystemStatus.State.OK, SystemStatus.State.TIMEOUT), Arrays.asList("entity1", "entity2"), null, null);
		assertThat(statusesQueryMatchingS1andS3).hasSize(3);
	}

	// @Test in clause with multiple columns not supported by h2
	public void testGetLatestStatusPerStatePerJob() throws Exception {

		SystemStatus first = new SystemStatus("job1", "1234567", SystemStatus.State.OK, now);
		repository.add(first);
		SystemStatus latest = new SystemStatus("job1", "1234568", SystemStatus.State.OK, DateUtils.addMinutes(now, 2));
		repository.add(latest);
		SystemStatus otherState = new SystemStatus("job1", "12345672", SystemStatus.State.STARTED, DateUtils.addMinutes(now, 3));
		repository.add(otherState);
		SystemStatus otherJob = new SystemStatus("job2", "12345633", SystemStatus.State.OK, DateUtils.addMinutes(now, 3));
		repository.add(otherJob);


		List<SystemStatus> statusList = repository.getLatestSystemStatus(null, null, null, null, null, null);
		Assert.assertEquals(3, statusList.size());
		Assert.assertFalse(statusList.contains(first));
		Assert.assertTrue(statusList.contains(latest));

		List<SystemStatus> filteredList = repository.getLatestSystemStatus(Arrays.asList("job1"), null,
				Arrays.asList(SystemStatus.State.OK), null, null, null);
		Assert.assertEquals(1, filteredList.size());
		Assert.assertTrue(filteredList.contains(latest));
	}


}
