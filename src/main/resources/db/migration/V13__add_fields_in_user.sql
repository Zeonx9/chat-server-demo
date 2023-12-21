ALTER TABLE users
    ADD is_online BOOLEAN;

ALTER TABLE users
    ADD patronymic VARCHAR(255);

ALTER TABLE users
    ADD phone_number VARCHAR(255);

ALTER TABLE chats
    ADD CONSTRAINT FK_CHATS_ON_LAST_MESSAGE FOREIGN KEY (last_message_id) REFERENCES messages (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES company (id);