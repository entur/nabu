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
