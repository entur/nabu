package no.rutebanken.nabu.rest.mapper;

import no.rutebanken.nabu.domain.Status;
import no.rutebanken.nabu.rest.domain.JobStatus;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class EnumMapperTest {

	@Test
	public void testEnumConversion() {
		List<Status.Action> converted = EnumMapper.convertEnums(Arrays.asList(JobStatus.Action.CLEAN, JobStatus.Action.BUILD_GRAPH), Status.Action.class);
		Assert.assertEquals(2, converted.size());
		Assert.assertTrue(converted.contains(Status.Action.BUILD_GRAPH));
		Assert.assertTrue(converted.contains(Status.Action.CLEAN));
	}

}
