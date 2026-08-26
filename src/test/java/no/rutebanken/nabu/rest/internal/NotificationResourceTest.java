package no.rutebanken.nabu.rest.internal;

import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.domain.event.JobState;
import no.rutebanken.nabu.event.filter.EventMatcher;
import no.rutebanken.nabu.event.filter.JobEventMatcher;
import no.rutebanken.nabu.event.user.dto.user.EventFilterDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * The advertised actions are compared to the action on a real event as plain strings, so a name that no producer
 * emits is selectable but can never match. These tests pin the advertised names against the ones marduk publishes.
 */
class NotificationResourceTest {

    @Test
    void advertisedGraphActionsMatchTheEventsMardukPublishes() {
        assertAdvertisedActionsMatch(JobEvent.JobDomain.GRAPH, List.of("OTP2_BUILD_GRAPH", "OTP2_BUILD_BASE"));
    }

    @Test
    void advertisedTimetablePublishActionsMatchTheEventsMardukPublishes() {
        assertAdvertisedActionsMatch(JobEvent.JobDomain.TIMETABLE_PUBLISH, List.of("EXPORT_NETEX_MERGED", "EXPORT_GTFS_MERGED"));
    }

    /**
     * The email template resolves the label with an empty default, so a missing key renders a blank action cell
     * instead of failing.
     */
    @ParameterizedTest
    @EnumSource(JobEvent.JobDomain.class)
    void everyAdvertisedActionHasAnEmailLabel(JobEvent.JobDomain jobDomain) throws Exception {
        Properties messages = new Properties();
        try (InputStream in = getClass().getResourceAsStream("/messages.properties")) {
            messages.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
        for (String action : advertisedActions(jobDomain)) {
            String key = "notification.email.jobevent.action." + action;
            Assertions.assertFalse(messages.getProperty(key, "").isBlank(), "missing " + key);
        }
    }

    private void assertAdvertisedActionsMatch(JobEvent.JobDomain jobDomain, List<String> publishedActions) {
        Set<String> advertised = advertisedActions(jobDomain);
        // equality, not containment: an advertised name no producer emits is selectable but can never match
        Assertions.assertEquals(new HashSet<>(publishedActions), advertised);

        EventFilterDTO filter = new EventFilterDTO();
        filter.jobDomain = jobDomain.toString();
        filter.states = Set.of(JobState.FAILED);
        filter.actions = advertised;

        for (String action : publishedActions) {
            JobEvent event = JobEvent.builder().domain(filter.jobDomain).state(JobState.FAILED).action(action).build();
            Assertions.assertTrue(new JobEventMatcher(filter).matches(event),
                    "no advertised " + jobDomain + " action matches a real marduk " + action + " event, advertised: " + advertised);
        }
    }

    /**
     * The wildcard matches anything, so leaving it in would make these assertions pass against any advertised name.
     */
    private Set<String> advertisedActions(JobEvent.JobDomain jobDomain) {
        Set<String> advertised = new HashSet<>(new NotificationResource().getJobActions(jobDomain));
        advertised.remove(EventMatcher.ALL_TYPES);
        return advertised;
    }
}
