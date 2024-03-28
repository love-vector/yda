create table if not exists users
(
    id       uuid         not null primary key,
    email    varchar(200) not null unique,
    password varchar(500) not null
);
