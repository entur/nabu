package no.rutebanken.nabu.organisation.repository;

import no.rutebanken.nabu.organisation.model.VersionedEntity;
import no.rutebanken.nabu.organisation.model.responsibility.ResponsibilitySet;

import java.util.List;

public interface ResponsibilitySetRepositoryCustom {
    List<ResponsibilitySet> getResponsibilitySetsReferringTo(VersionedEntity entity);

}
