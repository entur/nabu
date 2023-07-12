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

import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.event.user.dto.user.EventFilterDTO;


/**
 * Check whether an Event matches a given JobEventFilter.
 */
public class JobEventMatcher implements EventMatcher {

    private final EventFilterDTO filter;

    public JobEventMatcher(EventFilterDTO jobEventFilter) {
        this.filter = jobEventFilter;
    }

    @Override
    public boolean matches(Event event) {
        if (!(event instanceof JobEvent jobEvent)) {
            return false;
        }

        if (filter.getOrganisation() != null) {
            // TODO need to get providerid from organisationDTO for exact match
            if (jobEvent.getReferential() == null || !jobEvent.getReferential().endsWith(filter.getOrganisation().getPrivateCode().toLowerCase())) {
                return false;
            }

        }

        return filter.getJobDomain().equals(jobEvent.getDomain()) &&
                       filter.getStates().contains(jobEvent.getState()) &&
                       (filter.getActions().contains(ALL_TYPES) || filter.getActions().contains(jobEvent.getAction()));

    }
}
