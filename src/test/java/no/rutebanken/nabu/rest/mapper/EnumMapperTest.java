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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class EnumMapperTest {

    @Test
    void testEnumConversion() {
        List<TimeTableAction> converted = EnumMapper.convertEnums(Arrays.asList(TimeTableAction.CLEAN, TimeTableAction.OTP2_BUILD_GRAPH), TimeTableAction.class);
        Assertions.assertEquals(2, converted.size());
        Assertions.assertTrue(converted.contains(TimeTableAction.OTP2_BUILD_GRAPH));
        Assertions.assertTrue(converted.contains(TimeTableAction.CLEAN));
    }

}
