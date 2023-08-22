CREATE SEQUENCE event_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

select setval('event_seq',  (SELECT MAX(pk) + 1 FROM event));

ALTER TABLE event_seq OWNER TO nabu;

CREATE SEQUENCE notification_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

select setval('notification_seq',  (SELECT MAX(pk) + 1 FROM notification));

ALTER TABLE notification_seq OWNER TO nabu;

CREATE SEQUENCE system_job_status_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

select setval('system_job_status_seq',  (SELECT MAX(pk) + 1 FROM system_job_status));

ALTER TABLE system_job_status_seq OWNER TO nabu;
