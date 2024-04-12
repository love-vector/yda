create table if not exists information_nodes
(
    id              serial primary key,
    name            varchar(120) not null,
    collection_name varchar(200) not null unique,
    description     varchar,
    user_id         uuid         not null references chatbot.users (id),
    unique (user_id, name)
);

create index if not exists information_nodes_user_id on information_nodes (user_id);