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

package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.BaseIntegrationTest;
import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.JobState;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

public class SystemJobStatusRepositoryImplTest extends BaseIntegrationTest {

    @Autowired
    private SystemJobStatusRepository systemJobStatusRepository;


    @Test
    public void testFind() {

        SystemJobStatus s1 = new SystemJobStatus("dom1", "type1", JobState.PENDING, Instant.now());
        SystemJobStatus s2 = new SystemJobStatus("dom1", "type2", JobState.OK, Instant.now().plusMillis(1000));
        SystemJobStatus s3 = new SystemJobStatus("dom2", "type1", JobState.FAILED, Instant.now());

        systemJobStatusRepository.save(Arrays.asList(s1, s2, s3));

        Assert.assertEquals(3, systemJobStatusRepository.find(null, null).size());

        Assert.assertEquals(Arrays.asList(s3), systemJobStatusRepository.find(Arrays.asList("dom2"), null));
        Assert.assertEquals(Arrays.asList(s2), systemJobStatusRepository.find(new ArrayList<>(), Arrays.asList("type2")));
        Assert.assertEquals(Arrays.asList(s1), systemJobStatusRepository.find(Arrays.asList("dom1"), Arrays.asList("type1")));
    }
}
