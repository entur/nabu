-- Using a BRIN index since the events are "almost always" inserted in chronological order
CREATE INDEX i_event_time ON event USING BRIN (event_time);