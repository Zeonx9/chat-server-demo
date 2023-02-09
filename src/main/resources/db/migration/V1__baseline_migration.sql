CREATE TABLE users
(
    id       BIGINT       NOT NULL,
    name     VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_name UNIQUE (name);



CREATE TABLE chat_members
(
    chat_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_chat_members PRIMARY KEY (chat_id, user_id)
);

CREATE TABLE chats
(
    id         BIGINT NOT NULL,
    is_private BOOLEAN,
    CONSTRAINT pk_chats PRIMARY KEY (id)
);

ALTER TABLE chat_members
    ADD CONSTRAINT fk_chamem_on_chat FOREIGN KEY (chat_id) REFERENCES chats (id);

ALTER TABLE chat_members
    ADD CONSTRAINT fk_chamem_on_user FOREIGN KEY (user_id) REFERENCES users (id);

CREATE TABLE messages
(
    id        BIGINT NOT NULL,
    text      VARCHAR(255),
    date_time TIMESTAMP WITHOUT TIME ZONE,
    user_id   BIGINT,
    chat_id   BIGINT,
    CONSTRAINT pk_messages PRIMARY KEY (id)
);

ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_ON_CHAT FOREIGN KEY (chat_id) REFERENCES chats (id);

CREATE INDEX chat_id_index ON messages (chat_id);

ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
