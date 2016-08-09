insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (1, 'OST', 'ost', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32632','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (2, 'RUT', 'rut', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32632','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (3, 'HED', 'hed', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32632','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (4, 'OPP', 'opp', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32632','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (5, 'BRA', 'bra', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32632','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (6, 'VKT', 'vkt', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32632','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (7, 'AKT', 'akt', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32632','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (8, 'KOL', 'kol', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32632','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (9, 'HRD', 'hrd', 'Rutebanken', 'admin@rutebanken.org', null, null,'gtfs',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (10, 'SOF', 'sof', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32632','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (11, 'MOR', 'mor', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32632','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (12, 'ATB', 'atb', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32632','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (13, 'NTR', 'ntr', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32632','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (14, 'NOR', 'nor', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:4326','regtopp',null,true);  --THIS IS CORRECT
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (15, 'TRO', 'tro', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32633','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (16, 'FIN', 'fin', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32633','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (17, 'TOG', 'tog', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:4326','regtopp',null,true); --THIS IS ALSO CORRECT
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (18, 'TEL', 'tel', 'Rutebanken', 'admin@rutebanken.org', null, 'EPSG:32632','regtopp',null,true);
insert into public.chouette_info(id, prefix, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection, data_format,regtopp_calendar_strategy,enable_validation) values (19, 'NRI', 'nri', 'Rutebanken', 'admin@rutebanken.org', null, null,'gtfs',null,true);


insert into public.provider(id, name, sftp_account, chouette_info_id) values (1, 'Østfold / Østfold Kollektivtrafikk', 'ostfold', 1);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (2, 'Oslo og Akershus / Ruter', 'oslo-akershus', 2);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (3, 'Hedmark / Hedmark-Trafikk', 'hedmark', 3);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (4, 'Oppland / Opplandstrafikk', 'oppland', 4);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (5, 'Buskerud / Brakar', 'buskerud', 5);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (6, 'Vestfold / VkT', 'vestfold', 6);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (7, 'Agder / AkT', 'agder', 7);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (8, 'Rogaland / Kolumbus', 'rogaland', 8);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (9, 'Hordaland / Skyss', 'hordaland', 9);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (10, 'Sogn og Fjordane / Kringom', 'sogn-fjordane', 10);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (11, 'Møre og Romsdal / Fram', 'more-romsdal', 11);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (12, 'Sør-Trøndelag / AtB', 'sor-trondelag', 12);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (13, 'Nord-Trøndelag', 'nord-trondelag', 13);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (14, 'Nordland', 'nordland', 14);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (15, 'Troms / Troms Fylkestrafikk', 'troms', 15);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (16, 'Finnmark / Snelandia', 'finnmark', 16);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (17, 'Tog / NSB', 'tog', 17);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (18, 'Telemark', 'telemark', 18);
insert into public.provider(id, name, sftp_account, chouette_info_id) values (19, 'Norsk ReiseInformasjon', 'nri', 19);
