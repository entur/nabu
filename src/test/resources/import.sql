insert into status(correlation_id, provider_id, file_name, action, state, date) values ('123456789', '2', '00011-gtfs.zip', '1', '1', '2015-09-02 00:00:00Z')
insert into status(correlation_id, provider_id, file_name, action, state, date) values ('123456789', '2', '00012-gtfs.zip', '1', '2', '2015-09-02 12:45:00Z')
insert into status(correlation_id, provider_id, file_name, action, state, date) values ('1234567', '2', '00013-gtfs.zip', '2', '2', '2015-09-02 13:01:47Z')

insert into chouette_info(id, prefix, data_space, organisation, cuser) values (1, 'flybussekspressen', 'flybussekspressen', 'Rutebanken', 'admin@rutebanken.org');
insert into chouette_info(id, prefix, data_space, organisation, cuser) values (2, 'flybussekspressen2', 'flybussekspressen2', 'Rutebanken2', 'admin2@rutebanken.org');

insert into provider(id, name, sftp_account, chouette_info_id) values (42, 'Flybussekspressen', '42', 1);
insert into provider(id, name, chouette_info_id) values (43, 'Flybussekspressen2', 2);