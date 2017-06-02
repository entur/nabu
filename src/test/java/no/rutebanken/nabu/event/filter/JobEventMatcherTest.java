package no.rutebanken.nabu.event.filter;

import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.organisation.model.user.eventfilter.JobEventFilter;
import org.junit.Assert;
import org.junit.Test;

public class JobEventMatcherTest {

    @Test
    public void eventMatchingFilterWithoutOrganisationSet() {
        JobEventFilter filter = testFilter();
        Assert.assertTrue(new JobEventMatcher(filter).matches(matchingJobEvent(filter)));
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
        return JobEvent.builder().domain(filter.getJobDomain()).state(filter.getState()).action(filter.getAction()).build();
    }

    private JobEventFilter testFilter() {
        JobEventFilter filter = new JobEventFilter();
        filter.setJobDomain("testDomain");
        filter.setAction("testAction");
        filter.setState(JobState.FAILED);
        return filter;
    }
}
