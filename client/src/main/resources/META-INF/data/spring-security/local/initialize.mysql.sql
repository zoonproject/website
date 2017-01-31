drop table if exists authorities;
drop table if exists identity;
drop table if exists users;

create table users (
  username varchar(80) not null primary key,
  password varchar(60) not null,
  enabled tinyint(1) not null
) engine = InnoDb;

create table authorities (
  username varchar(80) not null,
  authority varchar(20) not null,
  foreign key (username) references users (username)
) engine = InnoDb;

commit;