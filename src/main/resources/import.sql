insert into public.chouette_info(prefix, data_space, organisation, cuser) values ('rutebanken', 'rutebanken', 'Rutebanken', 'admin@rutebanken.org');
insert into public.chouette_info(prefix, data_space, organisation, cuser) values ('tds1', 'testDS1', 'Rutebanken', 'admin@rutebanken.org');

insert into public.provider(id, name, sftp_account, chouette_info_id) values (42, 'Flybussekspressen', 'kartverk', 1);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (1, 'Rutebanken', 'nvdb', 2);