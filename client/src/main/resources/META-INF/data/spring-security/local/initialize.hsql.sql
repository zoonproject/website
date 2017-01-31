drop table authorities if exists;
drop table identity if exists;
drop table users if exists;

create table users (
  username varchar(80) not null,
  password varchar(60) not null,
  enabled boolean not null,
  constraint unique_username unique(username)
);

create table authorities (
  username varchar(80) not null,
  authority varchar(20) not null,
  constraint foreign_key_username foreign key(username) references users(username)
);

commit;