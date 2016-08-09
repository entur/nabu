package no.rutebanken.nabu.repository;

import java.util.List;

import no.rutebanken.nabu.domain.Status;

public interface StatusRepository {

    void add(Status status);

    List<Status> getStatusForProvider(Long providerId);

}
