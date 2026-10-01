-- Index for EventRepositoryImpl.getJobEventsForDomainAndProvider.
-- CONCURRENTLY must run outside Flyway's transaction. The build exceeds the
-- helm deploy timeout on a large table, so run this statement by hand before
-- deploying. An interrupted build leaves indisvalid=false; drop it by hand.
-- flyway:executeInTransaction=false
CREATE INDEX CONCURRENTLY IF NOT EXISTS i_event_domain_time ON event (domain, event_time);
