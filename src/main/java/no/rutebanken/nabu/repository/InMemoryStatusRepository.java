package no.rutebanken.nabu.repository;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import no.rutebanken.nabu.domain.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

//@Repository
public class InMemoryStatusRepository implements StatusRepository {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Table<Long, String, Status> statusTable = HashBasedTable.create();

    @Override
    public Status update(Status status){
        logger.debug("Updating status " + status);
        statusTable.put(Long.valueOf(status.providerId), status.correlationId, status);
        logger.debug("Added status " + status);
        return null;
    }

    @Override
    public Collection<Status> getStatusForProvider(Long providerId) {
        logger.debug("Getting status for provider '" + providerId + "'");
        Collection<Status> values = statusTable.row(providerId).values();
        logger.debug("Returning " + values);
        return values;
    }

}
