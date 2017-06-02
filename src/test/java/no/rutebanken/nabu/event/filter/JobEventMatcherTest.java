package no.rutebanken.nabu.event.filter;

import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.organisation.model.organisation.Authority;
import no.rutebanken.nabu.organisation.model.organisation.Organisation;
import no.rutebanken.nabu.organisation.model.user.eventfilter.JobEventFilter;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Or;

public class JobEventMatcherTest {

    @Test
    public void eventMatchingFilterWithoutOrganisationSet() {
        JobEventFilter filter = testFilter();
        Assert.assertTrue(new JobEventMatcher(filter).matches(matchingJobEvent(filter)));
    }

    @Test
    public void eventMatchingFilterWithOrganisationSet() {
        JobEventFilter filter = testFilter(organisation("KOK"));
        JobEvent orgSpaceEvent = matchingJobEvent(filter);
        Assert.assertTrue(new JobEventMatcher(filter).matches(orgSpaceEvent));

        JobEvent rbSpaceEvent = matchingJobEvent(filter);
        rbSpaceEvent.setReferential("rb_kok");
        Assert.assertTrue(new JobEventMatcher(filter).matches(rbSpaceEvent));
    }

    @Test
    public void eventWithoutRefNotNotMatchingFilterWithOrganisationSet() {
        JobEventFilter filter = testFilter(organisation("KOK"));
        JobEvent event = matchingJobEvent(filter);
        event.setReferential(null);
        Assert.assertFalse(new JobEventMatcher(filter).matches(event));
    }


    @Test
    public void eventWithOtherRefNotNotMatchingFilterWithOrganisationSet() {
        JobEventFilter filter = testFilter(organisation("KOK"));
        JobEvent event = matchingJobEvent(filter);
        event.setReferential("otherRef");
        Assert.assertFalse(new JobEventMatcher(filter).matches(event));
    }


    @Test
    public void eventWithDifferentJobDomainNotMatchingFilterWithoutOrganisationSet() {
        JobEventFilter filter = testFilter();
        JobEvent event = matchingJobEvent(filter);
        event.setDomain("otherDomain");
        Assert.assertFalse(new JobEventMatcher(filter).matches(event));
    }

    @Test
    public void eventWithDifferentActionNotMatchingFilterWithoutOrganisationSet() {
        JobEventFilter filter = testFilter();
        JobEvent event = matchingJobEvent(filter);
        event.setAction("otherAction");
        Assert.assertFalse(new JobEventMatcher(filter).matches(event));
    }

    @Test
    public void eventWithDifferentStateNotMatchingFilterWithoutOrganisationSet() {
        JobEventFilter filter = testFilter();
        JobEvent event = matchingJobEvent(filter);
        event.setState(JobState.PENDING);
        Assert.assertFalse(new JobEventMatcher(filter).matches(event));
    }

    @Test
    public void allStatesMatchingWildcardAction() {
        JobEventFilter filter = testFilter();
        filter.setAction(JobEventFilter.ALL_TYPES);
        JobEvent event = matchingJobEvent(filter);

        Assert.assertTrue(new JobEventMatcher(filter).matches(event));

        event.setAction("randomAction");
        Assert.assertTrue(new JobEventMatcher(filter).matches(event));
    }

    private JobEvent matchingJobEvent(JobEventFilter filter) {
        String ref = filter.getOrganisation() == null ? null : filter.getOrganisation().getPrivateCode().toLowerCase();
        return JobEvent.builder().domain(filter.getJobDomain()).referential(ref).state(filter.getState()).action(filter.getAction()).build();
    }

    private JobEventFilter testFilter() {
        return testFilter(null);
    }

    private JobEventFilter testFilter(Organisation organisation) {
        JobEventFilter filter = new JobEventFilter();
        filter.setJobDomain("testDomain");
        filter.setAction("testAction");
        filter.setOrganisation(organisation);
        filter.setState(JobState.FAILED);
        return filter;
    }

    private Organisation organisation(String privateCode) {
        Organisation org = new Authority();
        org.setPrivateCode(privateCode);
        return org;
    }
}
