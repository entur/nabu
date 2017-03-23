insert into status(id,correlation_id, provider_id, file_name, action, state, date) values (4030,'123456789', '2', '00011-gtfs.zip', '1', '1', '2015-09-02 00:00:00Z')
insert into status(id,correlation_id, provider_id, file_name, action, state, date) values (4031,'123456789', '2', '00012-gtfs.zip', '1', '2', '2015-09-02 12:45:00Z')
insert into status(id,correlation_id, provider_id, file_name, action, state, date) values (4032,'1234567', '2', '00013-gtfs.zip', '2', '2', '2015-09-02 13:01:47Z')

insert into chouette_info(id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection,data_format,enable_validation) values (1, 'flybussekspressen', 'http://www.ns.1','flybussekspressen', 'Rutebanken', 'admin@rutebanken.org', 'R12', 'EPSG:32632','regtopp',false);
insert into chouette_info(id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection,data_format,enable_validation) values (2, 'flybussekspressen2', 'http://www.ns.2','flybussekspressen2', 'Rutebanken2', 'admin2@rutebanken.org', 'R12', 'EPSG:32632','regtopp',false);

insert into provider(id, name, sftp_account, chouette_info_id) values (42, 'Flybussekspressen', '42', 1);
insert into provider(id, name, chouette_info_id) values (43, 'Flybussekspressen2', 2);


insert into code_space (pk, entity_version,lock_version,private_code, xmlns,xmlns_url) VALUES (1,1,1,'tst','TST','http://www.rutebanken.org/ns/tst');



insert into role (pk, entity_version,lock_version,private_code,name) VALUES (1,1,1,'testRole1','Test role 1');
insert into role (pk, entity_version,lock_version,private_code,name) VALUES (2,1,1,'testRole2','Test role 2');


insert into entity_type (pk, entity_version,lock_version,code_space_pk,private_code,name) VALUES (1,1,1,1,'StopPlace','Stoppesteder');
insert into entity_type (pk, entity_version,lock_version,code_space_pk,private_code,name) VALUES (2,1,1,1,'PlaceOfInterest','Place of interest');

insert into entity_classification (pk, entity_version,lock_version,entity_type_pk,code_space_pk,private_code,name) VALUES (1,1,1,1,1,'busStop','Bus stop');
insert into entity_classification (pk, entity_version,lock_version,entity_type_pk,code_space_pk,private_code,name) VALUES (2,1,1,1,1,'tramStop','Tram stop');
insert into entity_classification (pk, entity_version,lock_version,entity_type_pk,code_space_pk,private_code,name) VALUES (3,1,1,2,1,'*','All placeOfInterest');

insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('hibernate_sequence'),'Authority',1,1,1,'testOrg','Test Org');

insert into responsibility_role_assignment (pk,entity_version,lock_version,private_code,code_space_pk,responsible_organisation_pk,type_of_responsibility_role_pk) VALUES (1,1,1,'1',1,1,1);
insert into responsibility_role_assignment (pk,entity_version,lock_version,private_code,code_space_pk,responsible_organisation_pk,type_of_responsibility_role_pk) VALUES (2,1,1,'2',1,1,2);

insert into RESPONSIBILITY_ROLE_ASSIGNMENT_ENTITY_CLASSIFICATIONS (responsibility_role_assignment_pk, responsible_entity_classifications_pk) values (1,1);
insert into RESPONSIBILITY_ROLE_ASSIGNMENT_ENTITY_CLASSIFICATIONS (responsibility_role_assignment_pk, responsible_entity_classifications_pk) values (1,3);


insert into responsibility_set (pk,entity_version,lock_version,private_code,code_space_pk,name) values (1,1,1,'1',1,'Test rsp set');

insert into responsibility_set_roles (responsibility_Set_pk,roles_pk) values (1,1);
insert into responsibility_set_roles (responsibility_Set_pk,roles_pk) values (1,2);

alter sequence hibernate_sequence restart with 20000;
