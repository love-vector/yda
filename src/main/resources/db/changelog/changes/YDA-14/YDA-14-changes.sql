create table if not exists intentions
(
    id          serial primary key,
    name        varchar(80)  not null unique,
    definition  varchar(400) not null unique,
    description varchar,
    vector_id   uuid         not null unique
);
