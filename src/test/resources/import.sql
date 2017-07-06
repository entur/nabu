insert into chouette_info(id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection,data_format,enable_validation,enable_stop_place_update,enable_clean_import) values (1, 'flybussekspressen', 'http://www.ns.1','flybussekspressen', 'Rutebanken', 'admin@rutebanken.org', 'R12', 'EPSG:32632','regtopp',false,true,false);
insert into chouette_info(id, xmlns, xmlnsurl, referential, organisation, cuser, regtopp_version, regtopp_coordinate_projection,data_format,enable_validation,enable_stop_place_update,enable_clean_import) values (2, 'flybussekspressen2', 'http://www.ns.2','flybussekspressen2', 'Rutebanken2', 'admin2@rutebanken.org', 'R12', 'EPSG:32632','regtopp',false,true,false);

insert into provider(id, name, sftp_account, chouette_info_id) values (42, 'Flybussekspressen', '42', 1);
insert into provider(id, name, chouette_info_id) values (43, 'Flybussekspressen2', 2);


insert into code_space (pk, entity_version,lock_version,private_code, xmlns,xmlns_url) VALUES (1,1,1,'tst','TST','http://www.rutebanken.org/ns/tst');



insert into role (pk, entity_version,lock_version,private_code,name) VALUES (1,1,1,'testRole1','Test role 1');
insert into role (pk, entity_version,lock_version,private_code,name) VALUES (2,1,1,'testRole2','Test role 2');


insert into entity_type (pk, entity_version,lock_version,code_space_pk,private_code,name) VALUES (1,1,1,1,'StopPlaceType','Stop place types');
insert into entity_type (pk, entity_version,lock_version,code_space_pk,private_code,name) VALUES (2,1,1,1,'EntityType','Entity types');

insert into entity_classification (pk, entity_version,lock_version,entity_type_pk,code_space_pk,private_code,name) VALUES (1,1,1,1,1,'busStop','Bus stop');
insert into entity_classification (pk, entity_version,lock_version,entity_type_pk,code_space_pk,private_code,name) VALUES (2,1,1,1,1,'tramStop','Tram stop');
insert into entity_classification (pk, entity_version,lock_version,entity_type_pk,code_space_pk,private_code,name) VALUES (3,1,1,2,1,'*','All entity types');

insert into organisation(pk, dtype,entity_version,lock_version,code_space_pk,private_code,name) values (nextval('hibernate_sequence'),'Authority',1,1,1,'OrgTest','Test Org');

insert into responsibility_role_assignment (pk,entity_version,lock_version,private_code,code_space_pk,responsible_organisation_pk,type_of_responsibility_role_pk) VALUES (1,1,1,'1',1,1,1);
insert into responsibility_role_assignment (pk,entity_version,lock_version,private_code,code_space_pk,responsible_organisation_pk,type_of_responsibility_role_pk) VALUES (2,1,1,'2',1,1,2);
insert into responsibility_role_assignment (pk,entity_version,lock_version,private_code,code_space_pk,responsible_organisation_pk,type_of_responsibility_role_pk) VALUES (3,1,1,'3',1,1,2);

insert into ENTITY_CLASSIFICATION_ASSIGNMENT (pk,responsibility_role_assignment_pk, entity_classification_pk, allowed) values (1,1,1,'t');
insert into ENTITY_CLASSIFICATION_ASSIGNMENT (pk,responsibility_role_assignment_pk, entity_classification_pk, allowed) values (2,1,3,'t');


insert into responsibility_set (pk,entity_version,lock_version,private_code,code_space_pk,name) values (1,1,1,'RspSetTst',1,'Test rsp set');
insert into responsibility_set (pk,entity_version,lock_version,private_code,code_space_pk,name) values (2,1,1,'RspSetTst2',1,'Another rsp set');


insert into responsibility_set_roles (responsibility_Set_pk,roles_pk) values (1,1);
insert into responsibility_set_roles (responsibility_Set_pk,roles_pk) values (1,2);

insert into responsibility_set_roles (responsibility_Set_pk,roles_pk) values (2,3);

insert into contact_details (pk, email,first_name, last_name, phone) values (1,'test@test.org','First','Last','0047 23232323');
insert into user_account (pk,entity_version,lock_version,private_code,username,contact_details_pk,organisation_pk) values (1,1,1,'testUserCode','testuser',1,1);

alter sequence hibernate_sequence restart with 20000;
