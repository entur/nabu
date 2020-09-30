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

package no.rutebanken.nabu.event.filter;

import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.event.user.dto.organisation.OrganisationDTO;
import no.rutebanken.nabu.event.user.dto.user.EventFilterDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

class JobEventMatcherTest {

    @Test
    void eventMatchingFilterWithoutOrganisationSet() {
        EventFilterDTO filter = testFilter();
        Assertions.assertTrue(new JobEventMatcher(filter).matches(matchingJobEvent(filter)));
    }

    @Test
    void eventMatchingFilterWithOrganisationSet() {
        EventFilterDTO filter = testFilter(organisationDTO("KOK"));
        JobEvent orgSpaceEvent = matchingJobEvent(filter);
        Assertions.assertTrue(new JobEventMatcher(filter).matches(orgSpaceEvent));

        JobEvent rbSpaceEvent = matchingJobEvent(filter);
        rbSpaceEvent.setReferential("rb_kok");
        Assertions.assertTrue(new JobEventMatcher(filter).matches(rbSpaceEvent));
    }

    @Test
    void eventWithoutRefNotNotMatchingFilterWithOrganisationSet() {
        EventFilterDTO filter = testFilter(organisationDTO("KOK"));
        JobEvent event = matchingJobEvent(filter);
        event.setReferential(null);
        Assertions.assertFalse(new JobEventMatcher(filter).matches(event));
    }


    @Test
    void eventWithOtherRefNotNotMatchingFilterWithOrganisationSet() {
        EventFilterDTO filter = testFilter(organisationDTO("KOK"));
        JobEvent event = matchingJobEvent(filter);
        event.setReferential("otherRef");
        Assertions.assertFalse(new JobEventMatcher(filter).matches(event));
    }


    @Test
    void eventWithDifferentJobDomainNotMatchingFilterWithoutOrganisationSet() {
        EventFilterDTO filter = testFilter();
        JobEvent event = matchingJobEvent(filter);
        event.setDomain("otherDomain");
        Assertions.assertFalse(new JobEventMatcher(filter).matches(event));
    }

    @Test
    void eventWithDifferentActionNotMatchingFilterWithoutOrganisationSet() {
        EventFilterDTO filter = testFilter();
        JobEvent event = matchingJobEvent(filter);
        event.setAction("otherAction");
        Assertions.assertFalse(new JobEventMatcher(filter).matches(event));
    }

    @Test
    void eventWithDifferentStateNotMatchingFilterWithoutOrganisationSet() {
        EventFilterDTO filter = testFilter();
        JobEvent event = matchingJobEvent(filter);
        event.setState(JobState.PENDING);
        Assertions.assertFalse(new JobEventMatcher(filter).matches(event));
    }

    @Test
    void allStatesMatchingWildcardAction() {
        EventFilterDTO filter = testFilter();
        filter.actions = Set.of(EventMatcher.ALL_TYPES);
        JobEvent event = matchingJobEvent(filter);

        Assertions.assertTrue(new JobEventMatcher(filter).matches(event));

        event.setAction("randomAction");
        Assertions.assertTrue(new JobEventMatcher(filter).matches(event));
    }

    private JobEvent matchingJobEvent(EventFilterDTO filter) {
        String ref = filter.getOrganisation() == null ? null : filter.getOrganisation().getPrivateCode().toLowerCase();
        return JobEvent.builder().domain(filter.jobDomain).referential(ref).state(filter.states.iterator().next()).action(filter.actions.iterator().next()).build();
    }

    private EventFilterDTO testFilter() {
        return testFilter(null);
    }

    private EventFilterDTO testFilter(OrganisationDTO organisationDTO) {
        EventFilterDTO filter = new EventFilterDTO();
        filter.jobDomain = JobEvent.JobDomain.TIMETABLE.toString();
        filter.actions = Set.of("testAction");
        filter.organisation = organisationDTO;
        filter.states = Set.of(JobState.FAILED);
        return filter;
    }

    private OrganisationDTO organisationDTO(String privateCode) {
        OrganisationDTO org = new OrganisationDTO();
        org.privateCode = privateCode;
        return org;
    }
}
