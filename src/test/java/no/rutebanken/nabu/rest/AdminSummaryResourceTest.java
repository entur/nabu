/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.rutebanken.nabu.rest;


import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.rest.domain.SystemStatusAggregation;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class AdminSummaryResourceTest {

	@Test
	public void testConvertToSystemStatusAggregationEmptyCollection() {
		AdminSummaryResource resource = new AdminSummaryResource();
		Collection<SystemStatusAggregation> aggregations = resource.convertToSystemStatusAggregation(new ArrayList<>());
		Assert.assertTrue(aggregations.isEmpty());
	}

	@Test
	public void testConvertToSystemStatusAggregation() {
		AdminSummaryResource resource = new AdminSummaryResource();
		List<SystemJobStatus> statusList = new ArrayList<>();
		
		Instant now= Instant.now();
		
		statusList.add(new SystemJobStatus("job1", "", JobState.STARTED, now));
		statusList.add(new SystemJobStatus("job1", "", JobState.STARTED,now.plusMillis(2)));
		statusList.add(new SystemJobStatus("job1", "", JobState.STARTED, now.plusMillis(5)));
		statusList.add(new SystemJobStatus("job1", "", JobState.FAILED, now.plusMillis(3)));
		statusList.add(new SystemJobStatus("job2", "", JobState.OK, now.plusMillis(1)));

		Collection<SystemStatusAggregation> aggregations = resource.convertToSystemStatusAggregation(statusList);
		Assert.assertEquals(aggregations.size(), 2);

		SystemStatusAggregation agg1 = findAgg(aggregations, "job1");
		Assert.assertEquals(JobState.STARTED, agg1.currentState);
		Assert.assertEquals(Date.from(now.plusMillis(5)), agg1.currentStateDate);
		Assert.assertEquals(Date.from(now.plusMillis(3)), agg1.latestDatePerState.get(JobState.FAILED));


		SystemStatusAggregation agg2 = findAgg(aggregations, "job2");
		Assert.assertEquals(JobState.OK, agg2.currentState);
		Assert.assertEquals(Date.from(now.plusMillis(1)), agg2.currentStateDate);
		Assert.assertEquals(Date.from(now.plusMillis(1)), agg2.latestDatePerState.get(JobState.OK));

	}

	private SystemStatusAggregation findAgg(Collection<SystemStatusAggregation> aggregations, String entity) {
		for (SystemStatusAggregation agg : aggregations) {
			if (entity.equals(agg.jobDomain)) {
				return agg;
			}
		}
		return null;
	}
}
