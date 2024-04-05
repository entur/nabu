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

import no.rutebanken.nabu.event.user.AdministrativeZoneRepository;
import no.rutebanken.nabu.event.user.dto.user.EventFilterDTO;
import no.rutebanken.nabu.exceptions.NabuException;
import org.springframework.stereotype.Service;

@Service
public class EventMatcherFactory {

    private final AdministrativeZoneRepository administrativeZoneRepository;

    public EventMatcherFactory(AdministrativeZoneRepository administrativeZoneRepository) {
        this.administrativeZoneRepository = administrativeZoneRepository;
    }

    public EventMatcher createEventMatcher(EventFilterDTO eventFilter) {

        if (EventFilterDTO.EventFilterType.CRUD.equals(eventFilter.type)) {
            return new CrudEventMatcher(administrativeZoneRepository, eventFilter);
        } else if (EventFilterDTO.EventFilterType.JOB.equals(eventFilter.type)) {
            return new JobEventMatcher(eventFilter);
        }
        throw new NabuException("Unsupported eventFilter type: " + eventFilter.type);
    }
}
