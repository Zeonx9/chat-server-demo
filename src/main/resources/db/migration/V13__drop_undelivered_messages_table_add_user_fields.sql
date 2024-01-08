CREATE TABLE unread_counter
(
    count   INTEGER,
    user_id BIGINT NOT NULL,
    chat_id BIGINT NOT NULL,
    CONSTRAINT pk_unread_counter PRIMARY KEY (user_id, chat_id)
);

ALTER TABLE users
    ADD is_online BOOLEAN;

ALTER TABLE users
    ADD patronymic VARCHAR(255);

ALTER TABLE users
    ADD phone_number VARCHAR(255);

ALTER TABLE chats
    ADD CONSTRAINT FK_CHATS_ON_LAST_MESSAGE FOREIGN KEY (last_message_id) REFERENCES messages (id);

ALTER TABLE unread_counter
    ADD CONSTRAINT FK_UNREAD_COUNTER_ON_CHAT FOREIGN KEY (chat_id) REFERENCES chats (id);

ALTER TABLE unread_counter
    ADD CONSTRAINT FK_UNREAD_COUNTER_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES company (id);

ALTER TABLE messages_undelivered_to
    DROP CONSTRAINT fk_mesundto_on_message;

ALTER TABLE messages_undelivered_to
    DROP CONSTRAINT fk_mesundto_on_user;

DROP TABLE messages_undelivered_to CASCADE;