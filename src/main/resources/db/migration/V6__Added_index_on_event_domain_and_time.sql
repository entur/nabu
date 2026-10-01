-- Index for EventRepositoryImpl.getJobEventsForDomainAndProvider:
--   WHERE domain = ? AND event_time >= ? ORDER BY event_time DESC.
-- Both statements use CONCURRENTLY (no insert lock), so they must run outside
-- Flyway's transaction. DROP clears any invalid index a prior failed build
-- left; if that build was recorded as a failed migration, run `flyway repair`
-- before redeploying so this migration runs again.
-- flyway:executeInTransaction=false
DROP INDEX CONCURRENTLY IF EXISTS i_event_domain_time;
CREATE INDEX CONCURRENTLY i_event_domain_time ON event (domain, event_time);
