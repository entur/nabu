package no.rutebanken.nabu.rest.mapper;

import no.rutebanken.nabu.domain.event.TimeTableAction;
import no.rutebanken.nabu.rest.domain.JobStatus;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class EnumMapperTest {

    @Test
    public void testEnumConversion() {
        List<TimeTableAction> converted = EnumMapper.convertEnums(Arrays.asList(TimeTableAction.CLEAN, TimeTableAction.BUILD_GRAPH), TimeTableAction.class);
        Assert.assertEquals(2, converted.size());
        Assert.assertTrue(converted.contains(TimeTableAction.BUILD_GRAPH));
        Assert.assertTrue(converted.contains(TimeTableAction.CLEAN));
    }

}
