create table if not exists information_node
(
    id       uuid         not null primary key,
    name    varchar(200) not null,
    description varchar(500) not null,
    user_id uuid not null,
    FOREIGN KEY (user_id) REFERENCES chatbot.users (id)
    );
--
-- CREATE TABLE comments.contribution_comments
-- (
--     id              SERIAL PRIMARY KEY,
--     contribution_id BIGINT                   NOT NULL,
--     author_id       UUID                     NOT NULL,
--     parent_id       BIGINT, -- This field will be NULL for top-level comments
--     content         VARCHAR(255)             NOT NULL,
--     created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     is_edited       BOOLEAN                           DEFAULT FALSE,
--     root_id         BIGINT,
--     FOREIGN KEY (parent_id) REFERENCES comments.contribution_comments (id) ON DELETE CASCADE,
--     FOREIGN KEY (root_id) REFERENCES comments.contribution_comments (id) ON DELETE CASCADE
-- );
--
-- create table users.users
-- (
--     sub                   UUID primary key   not null,
--     nickname              varchar(30) unique not null,
--     bio                   varchar(255),
--     notification_email    varchar(200)       not null,
--     phone_number          varchar(15),
--     full_name             varchar(200),
--     notifications_enabled boolean            not null default true,
--     chall_sorting         varchar            not null,
--     is_paused             boolean            not null default false,
--     created_at            timestamptz        not null default now(),
--     updated_at            timestamptz
-- );
