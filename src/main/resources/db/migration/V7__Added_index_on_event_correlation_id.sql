-- The import progress endpoint looks events up by correlation id alone. i_event_provider leads on
-- provider_id and cannot serve that predicate, so without this the lookup scans the whole table.
-- CONCURRENTLY so the build cannot block the continuous Pub/Sub event ingestion the way a plain
-- CREATE INDEX would; it must therefore run outside Flyway's transaction. On a large table the
-- build can outlast the helm deploy timeout, so it can be built by hand ahead of the deploy and
-- IF NOT EXISTS makes this statement a no-op when that was done. See
-- docs/event-correlation-id-index-rollout.md. An interrupted build leaves indisvalid=false, which
-- this statement then skips silently; drop it by hand before deploying.
-- flyway:executeInTransaction=false
CREATE INDEX CONCURRENTLY IF NOT EXISTS i_event_correlation_id ON event USING btree (correlation_id);
