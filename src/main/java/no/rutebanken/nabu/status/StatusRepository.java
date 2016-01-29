package no.rutebanken.nabu.status;

import java.util.Collection;

public interface StatusRepository {

    void update(Status status);

    Collection<Status> getStatusForProvider(Long providerId);

}
