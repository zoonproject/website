insert into users(username, password, enabled) values ('afruit', 'appbanora', true);
insert into users(username, password, enabled) values ('atea', 'darjeassam', true);
insert into identities(username, identity) values ('afruit', 'A.P.Ple');
insert into identities(username, identity) values ('afruit', 'A P Ple');
insert into identities(username, identity) values ('atea', 'P.G Tips');
insert into email(identity, email) values (1, 'afruit@orchard.co.uk');
insert into email(identity, email) values (2, 'apple@gmail.com');
insert into email(identity, email) values (3, 'kettleson@hotmail.com');
insert into authorities(username, authority) values ('afruit', 'ROLE_USER');
insert into authorities(username, authority) values ('atea', 'ROLE_USER');

commit;