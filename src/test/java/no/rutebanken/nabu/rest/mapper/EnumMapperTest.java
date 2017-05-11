package no.rutebanken.nabu.rest.mapper;

import no.rutebanken.nabu.domain.event.TimeTableActionSubType;
import no.rutebanken.nabu.rest.domain.JobStatus;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class EnumMapperTest {

    @Test
    public void testEnumConversion() {
        List<TimeTableActionSubType> converted = EnumMapper.convertEnums(Arrays.asList(JobStatus.Action.CLEAN, JobStatus.Action.BUILD_GRAPH), TimeTableActionSubType.class);
        Assert.assertEquals(2, converted.size());
        Assert.assertTrue(converted.contains(TimeTableActionSubType.BUILD_GRAPH));
        Assert.assertTrue(converted.contains(TimeTableActionSubType.CLEAN));
    }

}
