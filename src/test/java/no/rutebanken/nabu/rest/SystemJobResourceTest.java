package no.rutebanken.nabu.rest;


import no.rutebanken.nabu.domain.SystemStatus;
import no.rutebanken.nabu.rest.domain.SystemStatusAggregation;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class SystemJobResourceTest {


	@Test
	public void testConvertToSystemStatusAggregationEmptyCollection() {
		SystemJobResource resource = new SystemJobResource();
		Collection<SystemStatusAggregation> aggregations = resource.convertToSystemStatusAggregation(new ArrayList<>());
		Assert.assertTrue(aggregations.isEmpty());
	}

	@Test
	public void testConvertToSystemStatusAggregation() {
		SystemJobResource resource = new SystemJobResource();
		List<SystemStatus> statusList = new ArrayList<>();
		statusList.add(new SystemStatus("job1", "", SystemStatus.State.STARTED, new Date(0)));
		statusList.add(new SystemStatus("job1", "", SystemStatus.State.STARTED, new Date(2)));
		statusList.add(new SystemStatus("job1", "", SystemStatus.State.STARTED, new Date(5)));
		statusList.add(new SystemStatus("job1", "", SystemStatus.State.FAILED, new Date(3)));
		statusList.add(new SystemStatus("job2", "", SystemStatus.State.OK, new Date(1)));

		Collection<SystemStatusAggregation> aggregations = resource.convertToSystemStatusAggregation(statusList);
		Assert.assertEquals(aggregations.size(), 2);

		SystemStatusAggregation agg1 = findAgg(aggregations, "job1");
		Assert.assertEquals(SystemStatus.State.STARTED, agg1.currentState);
		Assert.assertEquals(new Date(5), agg1.currentStateDate);
		Assert.assertEquals(new Date(3), agg1.latestDatePerState.get(SystemStatus.State.FAILED));


		SystemStatusAggregation agg2 = findAgg(aggregations, "job2");
		Assert.assertEquals(SystemStatus.State.OK, agg2.currentState);
		Assert.assertEquals(new Date(1), agg2.currentStateDate);
		Assert.assertEquals(new Date(1), agg2.latestDatePerState.get(SystemStatus.State.OK));

	}

	private SystemStatusAggregation findAgg(Collection<SystemStatusAggregation> aggregations, String jobType) {
		for (SystemStatusAggregation agg : aggregations) {
			if (jobType.equals(agg.jobType)) {
				return agg;
			}
		}
		return null;
	}
}
