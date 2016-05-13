insert into public.chouette_info(id, prefix, data_space, organisation, cuser, regtopp_version, regtopp_coordinate_projection) values (1, 'rutebanken', 'rutebanken', 'Rutebanken', 'admin@rutebanken.org', 'R12', 'EPSG:32632');
insert into public.chouette_info(id, prefix, data_space, organisation, cuser, regtopp_version, regtopp_coordinate_projection) values (2, 'tds1', 'testDS1', 'Rutebanken', 'admin@rutebanken.org', 'R12', 'EPSG:32632');

insert into public.provider(id, name, sftp_account, chouette_info_id) values (42, 'Flybussekspressen', 'kartverk', 1);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (1, 'Rutebanken', 'nvdb', 2);