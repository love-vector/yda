create table if not exists assistants
(
    id           serial primary key,
    name         varchar(50) not null,
    instructions varchar     not null,
    created_at   timestamptz not null,
    assistant_id varchar(36) not null unique,
    user_id      uuid        not null references yda.users (id),
    UNIQUE (user_id, name)
);

create index if not exists assistants_user_id on yda.assistants (user_id);

create table if not exists threads
(
    id           serial primary key,
    created_at   timestamptz not null,
    thread_id    varchar(36) not null unique,
    assistant_id bigint      not null references yda.assistants (id)
);

create index if not exists threads_assistant_id on yda.threads (assistant_id);
