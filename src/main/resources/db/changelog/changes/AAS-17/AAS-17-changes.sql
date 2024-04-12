create table if not exists assistant
(
    id       varchar(36) not null primary key,
    name     varchar(50) not null,
    instructions  TEXT  not null,
    created_at       TIMESTAMP WITH TIME ZONE,
    user_id  UUID not null,
    FOREIGN KEY (user_id) REFERENCES chatbot.users (id),
    UNIQUE (user_id, name)
);

create table if not exists thread
(
    id       varchar(36) not null primary key,
    created_at       TIMESTAMP WITH TIME ZONE
);