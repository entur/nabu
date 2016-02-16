package no.rutebanken.nabu.repository;

import no.rutebanken.nabu.domain.Status;

import java.util.Collection;

public interface StatusRepository {

    Status update(Status status);

    Collection<Status> getStatusForProvider(Long providerId);

}
