package no.rutebanken.nabu.jms;

import no.rutebanken.nabu.domain.SystemStatus;
import no.rutebanken.nabu.repository.SystemStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class SystemStatusListener {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SystemStatusRepository systemStatusRepository;


	@JmsListener(destination = "MardukSystemStatusQueue")
	public void processMessage(String content) {
		SystemStatus systemStatus = SystemStatus.fromString(content);
		logger.info("Received system status event: " + systemStatus.toString());
		systemStatusRepository.add(systemStatus);
	}
}
