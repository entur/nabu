--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.2
-- Dumped by pg_dump version 9.5.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: postgis; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS postgis WITH SCHEMA public;


--
-- Name: EXTENSION postgis; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION postgis IS 'PostGIS geometry, geography, and raster spatial types and functions';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: event; Type: TABLE; Schema: public; Owner: nabu
--

CREATE TABLE event (
    dtype character varying(31) NOT NULL,
    pk bigint NOT NULL,
    action character varying(255) NOT NULL,
    correlation_id character varying(255),
    event_time timestamp without time zone NOT NULL,
    external_id character varying(255),
    name character varying(255),
    registered_time timestamp without time zone NOT NULL,
    username character varying(255),
    change_type character varying(255),
    comment character varying(255),
    entity_classifier character varying(255),
    entity_type character varying(255),
    geometry geometry,
    new_value character varying(255),
    old_value character varying(255),
    version bigint,
    domain character varying(255),
    provider_id bigint,
    referential character varying(255),
    state integer
);


ALTER TABLE event OWNER TO nabu;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: nabu
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE hibernate_sequence OWNER TO nabu;

--
-- Name: notification; Type: TABLE; Schema: public; Owner: nabu
--

CREATE TABLE notification (
    pk bigint NOT NULL,
    status integer,
    type integer,
    user_name character varying(255),
    event_pk bigint
);


ALTER TABLE notification OWNER TO nabu;

--
-- Name: qrtz_blob_triggers; Type: TABLE; Schema: public; Owner: nabu
--

CREATE TABLE qrtz_blob_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    blob_data bytea
);


ALTER TABLE qrtz_blob_triggers OWNER TO nabu;

--
-- Name: qrtz_calendars; Type: TABLE; Schema: public; Owner: nabu
--

CREATE TABLE qrtz_calendars (
    sched_name character varying(120) NOT NULL,
    calendar_name character varying(200) NOT NULL,
    calendar bytea NOT NULL
);


ALTER TABLE qrtz_calendars OWNER TO nabu;

--
-- Name: qrtz_cron_triggers; Type: TABLE; Schema: public; Owner: nabu
--

CREATE TABLE qrtz_cron_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    cron_expression character varying(120) NOT NULL,
    time_zone_id character varying(80)
);


ALTER TABLE qrtz_cron_triggers OWNER TO nabu;

--
-- Name: qrtz_fired_triggers; Type: TABLE; Schema: public; Owner: nabu
--

CREATE TABLE qrtz_fired_triggers (
    sched_name character varying(120) NOT NULL,
    entry_id character varying(95) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    instance_name character varying(200) NOT NULL,
    fired_time bigint NOT NULL,
    sched_time bigint NOT NULL,
    priority integer NOT NULL,
    state character varying(16) NOT NULL,
    job_name character varying(200),
    job_group character varying(200),
    is_nonconcurrent boolean,
    requests_recovery boolean
);


ALTER TABLE qrtz_fired_triggers OWNER TO nabu;

--
-- Name: qrtz_job_details; Type: TABLE; Schema: public; Owner: nabu
--

CREATE TABLE qrtz_job_details (
    sched_name character varying(120) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    job_class_name character varying(250) NOT NULL,
    is_durable boolean NOT NULL,
    is_nonconcurrent boolean NOT NULL,
    is_update_data boolean NOT NULL,
    requests_recovery boolean NOT NULL,
    job_data bytea
);


ALTER TABLE qrtz_job_details OWNER TO nabu;

--
-- Name: qrtz_locks; Type: TABLE; Schema: public; Owner: nabu
--

CREATE TABLE qrtz_locks (
    sched_name character varying(120) NOT NULL,
    lock_name character varying(40) NOT NULL
);


ALTER TABLE qrtz_locks OWNER TO nabu;

--
-- Name: qrtz_paused_trigger_grps; Type: TABLE; Schema: public; Owner: nabu
--

CREATE TABLE qrtz_paused_trigger_grps (
    sched_name character varying(120) NOT NULL,
    trigger_group character varying(200) NOT NULL
);


ALTER TABLE qrtz_paused_trigger_grps OWNER TO nabu;

--
-- Name: qrtz_scheduler_state; Type: TABLE; Schema: public; Owner: nabu
--

CREATE TABLE qrtz_scheduler_state (
    sched_name character varying(120) NOT NULL,
    instance_name character varying(200) NOT NULL,
    last_checkin_time bigint NOT NULL,
    checkin_interval bigint NOT NULL
);


ALTER TABLE qrtz_scheduler_state OWNER TO nabu;

--
-- Name: qrtz_simple_triggers; Type: TABLE; Schema: public; Owner: nabu
--

CREATE TABLE qrtz_simple_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    repeat_count bigint NOT NULL,
    repeat_interval bigint NOT NULL,
    times_triggered bigint NOT NULL
);


ALTER TABLE qrtz_simple_triggers OWNER TO nabu;

--
-- Name: qrtz_simprop_triggers; Type: TABLE; Schema: public; Owner: nabu
--

CREATE TABLE qrtz_simprop_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    str_prop_1 character varying(512),
    str_prop_2 character varying(512),
    str_prop_3 character varying(512),
    int_prop_1 integer,
    int_prop_2 integer,
    long_prop_1 bigint,
    long_prop_2 bigint,
    dec_prop_1 numeric(13,4),
    dec_prop_2 numeric(13,4),
    bool_prop_1 boolean,
    bool_prop_2 boolean
);


ALTER TABLE qrtz_simprop_triggers OWNER TO nabu;

--
-- Name: qrtz_triggers; Type: TABLE; Schema: public; Owner: nabu
--

CREATE TABLE qrtz_triggers (
    sched_name character varying(120) NOT NULL,
    trigger_name character varying(200) NOT NULL,
    trigger_group character varying(200) NOT NULL,
    job_name character varying(200) NOT NULL,
    job_group character varying(200) NOT NULL,
    description character varying(250),
    next_fire_time bigint,
    prev_fire_time bigint,
    priority integer,
    trigger_state character varying(16) NOT NULL,
    trigger_type character varying(8) NOT NULL,
    start_time bigint NOT NULL,
    end_time bigint,
    calendar_name character varying(200),
    misfire_instr smallint,
    job_data bytea
);


ALTER TABLE qrtz_triggers OWNER TO nabu;


--
-- Name: system_job_status; Type: TABLE; Schema: public; Owner: nabu
--

CREATE TABLE system_job_status (
    pk bigint NOT NULL,
    action character varying(255),
    job_domain character varying(255),
    last_status_time timestamp without time zone,
    state integer
);


ALTER TABLE system_job_status OWNER TO nabu;

--
-- Name: event_pkey; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY event
    ADD CONSTRAINT event_pkey PRIMARY KEY (pk);


--
-- Name: notification_pkey; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY notification
    ADD CONSTRAINT notification_pkey PRIMARY KEY (pk);


--
-- Name: qrtz_blob_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_blob_triggers
    ADD CONSTRAINT qrtz_blob_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_calendars_pkey; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_calendars
    ADD CONSTRAINT qrtz_calendars_pkey PRIMARY KEY (sched_name, calendar_name);


--
-- Name: qrtz_cron_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_cron_triggers
    ADD CONSTRAINT qrtz_cron_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_fired_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_fired_triggers
    ADD CONSTRAINT qrtz_fired_triggers_pkey PRIMARY KEY (sched_name, entry_id);


--
-- Name: qrtz_job_details_pkey; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_job_details
    ADD CONSTRAINT qrtz_job_details_pkey PRIMARY KEY (sched_name, job_name, job_group);


--
-- Name: qrtz_locks_pkey; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_locks
    ADD CONSTRAINT qrtz_locks_pkey PRIMARY KEY (sched_name, lock_name);


--
-- Name: qrtz_paused_trigger_grps_pkey; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_paused_trigger_grps
    ADD CONSTRAINT qrtz_paused_trigger_grps_pkey PRIMARY KEY (sched_name, trigger_group);


--
-- Name: qrtz_scheduler_state_pkey; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_scheduler_state
    ADD CONSTRAINT qrtz_scheduler_state_pkey PRIMARY KEY (sched_name, instance_name);


--
-- Name: qrtz_simple_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_simple_triggers
    ADD CONSTRAINT qrtz_simple_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_simprop_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_simprop_triggers
    ADD CONSTRAINT qrtz_simprop_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_triggers
    ADD CONSTRAINT qrtz_triggers_pkey PRIMARY KEY (sched_name, trigger_name, trigger_group);

--
-- Name: system_job_status_pkey; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY system_job_status
    ADD CONSTRAINT system_job_status_pkey PRIMARY KEY (pk);


--
-- Name: system_job_status_unique; Type: CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY system_job_status
    ADD CONSTRAINT system_job_status_unique UNIQUE (job_domain, action, state);


--
-- Name: i_event_provider; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX i_event_provider ON event USING btree (provider_id, correlation_id, action, event_time);


--
-- Name: idx_qrtz_ft_inst_job_req_rcvry; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_ft_inst_job_req_rcvry ON qrtz_fired_triggers USING btree (sched_name, instance_name, requests_recovery);


--
-- Name: idx_qrtz_ft_j_g; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_ft_j_g ON qrtz_fired_triggers USING btree (sched_name, job_name, job_group);


--
-- Name: idx_qrtz_ft_jg; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_ft_jg ON qrtz_fired_triggers USING btree (sched_name, job_group);


--
-- Name: idx_qrtz_ft_t_g; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_ft_t_g ON qrtz_fired_triggers USING btree (sched_name, trigger_name, trigger_group);


--
-- Name: idx_qrtz_ft_tg; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_ft_tg ON qrtz_fired_triggers USING btree (sched_name, trigger_group);


--
-- Name: idx_qrtz_ft_trig_inst_name; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_ft_trig_inst_name ON qrtz_fired_triggers USING btree (sched_name, instance_name);


--
-- Name: idx_qrtz_j_grp; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_j_grp ON qrtz_job_details USING btree (sched_name, job_group);


--
-- Name: idx_qrtz_j_req_recovery; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_j_req_recovery ON qrtz_job_details USING btree (sched_name, requests_recovery);


--
-- Name: idx_qrtz_t_c; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_t_c ON qrtz_triggers USING btree (sched_name, calendar_name);


--
-- Name: idx_qrtz_t_g; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_t_g ON qrtz_triggers USING btree (sched_name, trigger_group);


--
-- Name: idx_qrtz_t_j; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_t_j ON qrtz_triggers USING btree (sched_name, job_name, job_group);


--
-- Name: idx_qrtz_t_jg; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_t_jg ON qrtz_triggers USING btree (sched_name, job_group);


--
-- Name: idx_qrtz_t_n_g_state; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_t_n_g_state ON qrtz_triggers USING btree (sched_name, trigger_group, trigger_state);


--
-- Name: idx_qrtz_t_n_state; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_t_n_state ON qrtz_triggers USING btree (sched_name, trigger_name, trigger_group, trigger_state);


--
-- Name: idx_qrtz_t_next_fire_time; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_t_next_fire_time ON qrtz_triggers USING btree (sched_name, next_fire_time);


--
-- Name: idx_qrtz_t_nft_misfire; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_t_nft_misfire ON qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time);


--
-- Name: idx_qrtz_t_nft_st; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_t_nft_st ON qrtz_triggers USING btree (sched_name, trigger_state, next_fire_time);


--
-- Name: idx_qrtz_t_nft_st_misfire; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_t_nft_st_misfire ON qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_state);


--
-- Name: idx_qrtz_t_nft_st_misfire_grp; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_t_nft_st_misfire_grp ON qrtz_triggers USING btree (sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);


--
-- Name: idx_qrtz_t_state; Type: INDEX; Schema: public; Owner: nabu
--

CREATE INDEX idx_qrtz_t_state ON qrtz_triggers USING btree (sched_name, trigger_state);

--
-- Name: fkaywskei3kprrnjim4dcayj5vh; Type: FK CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY notification
    ADD CONSTRAINT fkaywskei3kprrnjim4dcayj5vh FOREIGN KEY (event_pk) REFERENCES event(pk);


--
-- Name: qrtz_blob_triggers_sched_name_fkey; Type: FK CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_blob_triggers
    ADD CONSTRAINT qrtz_blob_triggers_sched_name_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_cron_triggers_sched_name_fkey; Type: FK CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_cron_triggers
    ADD CONSTRAINT qrtz_cron_triggers_sched_name_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_simple_triggers_sched_name_fkey; Type: FK CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_simple_triggers
    ADD CONSTRAINT qrtz_simple_triggers_sched_name_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_simprop_triggers_sched_name_fkey; Type: FK CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_simprop_triggers
    ADD CONSTRAINT qrtz_simprop_triggers_sched_name_fkey FOREIGN KEY (sched_name, trigger_name, trigger_group) REFERENCES qrtz_triggers(sched_name, trigger_name, trigger_group);


--
-- Name: qrtz_triggers_sched_name_fkey; Type: FK CONSTRAINT; Schema: public; Owner: nabu
--

ALTER TABLE ONLY qrtz_triggers
    ADD CONSTRAINT qrtz_triggers_sched_name_fkey FOREIGN KEY (sched_name, job_name, job_group) REFERENCES qrtz_job_details(sched_name, job_name, job_group);

--
-- PostgreSQL database dump complete
--

