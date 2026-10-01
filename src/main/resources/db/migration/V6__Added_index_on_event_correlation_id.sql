-- The import progress endpoint looks events up by correlation id alone. i_event_provider leads on
-- provider_id and cannot serve that predicate, so without this the lookup scans the whole table.
-- IF NOT EXISTS so a large table can be indexed ahead of the deploy with CREATE INDEX CONCURRENTLY.
CREATE INDEX IF NOT EXISTS i_event_correlation_id ON event USING btree (correlation_id);
