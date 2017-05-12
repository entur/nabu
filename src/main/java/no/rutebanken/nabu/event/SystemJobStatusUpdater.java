package no.rutebanken.nabu.event;

import no.rutebanken.nabu.domain.SystemJobStatus;
import no.rutebanken.nabu.domain.event.Event;
import no.rutebanken.nabu.domain.event.JobEvent;
import no.rutebanken.nabu.repository.SystemJobStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

/**
 * Update system job state for relevant events (ie JobEvents without provider).
 */
@Service
public class SystemJobStatusUpdater implements EventHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SystemJobStatusRepository systemJobStatusRepository;

    @Override
    public void onEvent(Event event) {
        if (event instanceof JobEvent) {
            updateSystemJobStatus((JobEvent) event);
        }
    }

    private void updateSystemJobStatus(JobEvent jobEvent) {
        if (jobEvent.getProviderId() == null) {

            SystemJobStatus systemJobStatus = new SystemJobStatus(jobEvent.getDomain(), jobEvent.getAction(), jobEvent.getState(), null);
            SystemJobStatus existingStatus = systemJobStatusRepository.findOne(Example.of(systemJobStatus));

            systemJobStatus.setLastStatusTime(jobEvent.getEventTime());
            if (existingStatus == null) {
                logger.info("Registering new system status from incoming event: " + systemJobStatus);
                systemJobStatusRepository.save(systemJobStatus);
            } else if (existingStatus.getLastStatusTime().isBefore(systemJobStatus.getLastStatusTime())) {
                logger.debug("Updating system status from incoming event: " + systemJobStatus);
                systemJobStatusRepository.save(existingStatus);
            }

        }

    }
}
