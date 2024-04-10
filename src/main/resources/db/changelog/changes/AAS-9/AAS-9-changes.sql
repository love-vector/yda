create table if not exists information_node
(
    id              uuid         not null primary key,
    name            varchar(200) not null,
    collection_name varchar(200) not null unique,
    description     varchar(500) not null,
    user_id         uuid         not null,
    FOREIGN KEY (user_id) REFERENCES chatbot.users (id),
    UNIQUE (user_id, name)
);