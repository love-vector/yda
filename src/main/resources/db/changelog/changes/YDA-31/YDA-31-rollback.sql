create table assistants
(
    id           serial primary key,
    name         varchar(50) not null unique,
    instructions varchar     not null,
    created_at   timestamptz not null,
    assistant_id varchar(36) not null unique
);

create table threads
(
    id           serial primary key,
    created_at   timestamptz not null,
    thread_id    varchar(36) not null unique,
    assistant_id bigint      not null references assistants
);

create index threads_assistant_id on threads (assistant_id);
