insert into status(id,correlation_id, provider_id, file_name, action, state, date) values (4030,'123456789', '2', '00011-gtfs.zip', '1', '1', '2015-09-02 00:00:00Z')
insert into status(id,correlation_id, provider_id, file_name, action, state, date) values (4031,'123456789', '2', '00012-gtfs.zip', '1', '2', '2015-09-02 12:45:00Z')
insert into status(id,correlation_id, provider_id, file_name, action, state, date) values (4032,'1234567', '2', '00013-gtfs.zip', '2', '2', '2015-09-02 13:01:47Z')

insert into chouette_info(id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection,data_format,enable_validation) values (4031, 'flybussekspressen', 'http://www.ns.1','flybussekspressen', 'Rutebanken', 'admin@rutebanken.org', 'R12', 'EPSG:32632','regtopp',false);
insert into chouette_info(id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection,data_format,enable_validation) values (4032, 'flybussekspressen2', 'http://www.ns.2','flybussekspressen2', 'Rutebanken2', 'admin2@rutebanken.org', 'R12', 'EPSG:32632','regtopp',false);

insert into provider(id, name, sftp_account, chouette_info_id) values (4042, 'Flybussekspressen', '42', 4031);
insert into provider(id, name, chouette_info_id) values (4043, 'Flybussekspressen2', 4032);



