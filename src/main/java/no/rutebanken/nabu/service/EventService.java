package no.rutebanken.nabu.service;

import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.repository.EventRepository;
import no.rutebanken.nabu.repository.SystemJobStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SystemJobStatusRepository systemJobStatusRepository;

    public void addEvent(Event event) {
        eventRepository.save(event);

        if (event instanceof JobEvent) {
            updateSystemJobStatus((JobEvent) event);
        }
    }


    private void updateSystemJobStatus(JobEvent jobEvent) {
        if (jobEvent.getProviderId() == null) {

            SystemJobStatus systemJobStatus = new SystemJobStatus(jobEvent.getDomain(), jobEvent.getActionSubType(), jobEvent.getState(), null);
            SystemJobStatus existingStatus = systemJobStatusRepository.findOne(Example.of(systemJobStatus));

            systemJobStatus.setLastStatusTime(jobEvent.getEventTime());
            if (existingStatus == null) {
                systemJobStatusRepository.save(systemJobStatus);
            } else if (existingStatus.getLastStatusTime().isBefore(systemJobStatus.getLastStatusTime())) {
                existingStatus.setLastStatusTime(systemJobStatus.getLastStatusTime());
                systemJobStatusRepository.save(existingStatus);
            }

        }

    }

}
