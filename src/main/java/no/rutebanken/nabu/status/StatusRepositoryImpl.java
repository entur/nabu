package no.rutebanken.nabu.status;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository     //TODO Implement persistence
public class StatusRepositoryImpl implements StatusRepository {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<Long, Map<String, Status>> statusMap = new HashMap();

    private Table<Long, String, Status> statusTable = HashBasedTable.create();

    public void update(Status status){
        logger.debug("Updating status " + status);
        statusTable.put(Long.valueOf(status.providerId), status.correlationId, status);
        logger.debug("Added status " + status);
    }

    public Collection<Status> getStatusForProvider(Long providerId) {
        logger.debug("Getting status for provider '" + providerId + "'");
        Collection<Status> values = statusTable.row(providerId).values();
        logger.debug("Returning " + values);
        return values;
    }

}
