package no.rutebanken.nabu.event.filter;

import com.google.common.collect.Sets;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.event.user.dto.organisation.OrganisationDTO;
import no.rutebanken.nabu.event.user.dto.user.EventFilterDTO;
import org.junit.Assert;
import org.junit.Test;

public class JobEventMatcherTest {

    @Test
    public void eventMatchingFilterWithoutOrganisationSet() {
        EventFilterDTO filter = testFilter();
        Assert.assertTrue(new JobEventMatcher(filter).matches(matchingJobEvent(filter)));
    }

    @Test
    public void eventMatchingFilterWithOrganisationSet() {
        EventFilterDTO filter = testFilter(organisationDTO("KOK"));
        JobEvent orgSpaceEvent = matchingJobEvent(filter);
        Assert.assertTrue(new JobEventMatcher(filter).matches(orgSpaceEvent));

        JobEvent rbSpaceEvent = matchingJobEvent(filter);
        rbSpaceEvent.setReferential("rb_kok");
        Assert.assertTrue(new JobEventMatcher(filter).matches(rbSpaceEvent));
    }

    @Test
    public void eventWithoutRefNotNotMatchingFilterWithOrganisationSet() {
        EventFilterDTO filter = testFilter(organisationDTO("KOK"));
        JobEvent event = matchingJobEvent(filter);
        event.setReferential(null);
        Assert.assertFalse(new JobEventMatcher(filter).matches(event));
    }


    @Test
    public void eventWithOtherRefNotNotMatchingFilterWithOrganisationSet() {
        EventFilterDTO filter = testFilter(organisationDTO("KOK"));
        JobEvent event = matchingJobEvent(filter);
        event.setReferential("otherRef");
        Assert.assertFalse(new JobEventMatcher(filter).matches(event));
    }


    @Test
    public void eventWithDifferentJobDomainNotMatchingFilterWithoutOrganisationSet() {
        EventFilterDTO filter = testFilter();
        JobEvent event = matchingJobEvent(filter);
        event.setDomain("otherDomain");
        Assert.assertFalse(new JobEventMatcher(filter).matches(event));
    }

    @Test
    public void eventWithDifferentActionNotMatchingFilterWithoutOrganisationSet() {
        EventFilterDTO filter = testFilter();
        JobEvent event = matchingJobEvent(filter);
        event.setAction("otherAction");
        Assert.assertFalse(new JobEventMatcher(filter).matches(event));
    }

    @Test
    public void eventWithDifferentStateNotMatchingFilterWithoutOrganisationSet() {
        EventFilterDTO filter = testFilter();
        JobEvent event = matchingJobEvent(filter);
        event.setState(JobState.PENDING);
        Assert.assertFalse(new JobEventMatcher(filter).matches(event));
    }

    @Test
    public void allStatesMatchingWildcardAction() {
        EventFilterDTO filter = testFilter();
        filter.actions = Sets.newHashSet(EventMatcher.ALL_TYPES);
        JobEvent event = matchingJobEvent(filter);

        Assert.assertTrue(new JobEventMatcher(filter).matches(event));

        event.setAction("randomAction");
        Assert.assertTrue(new JobEventMatcher(filter).matches(event));
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
        filter.actions = (Sets.newHashSet("testAction"));
        filter.organisation = organisationDTO;
        filter.states = Sets.newHashSet(JobState.FAILED);
        return filter;
    }

    private OrganisationDTO organisationDTO(String privateCode) {
        OrganisationDTO org = new OrganisationDTO();
        org.privateCode = privateCode;
        return org;
    }
}
