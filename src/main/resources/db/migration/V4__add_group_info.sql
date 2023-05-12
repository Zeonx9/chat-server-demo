CREATE SEQUENCE IF NOT EXISTS group_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE "group"
(
    id            BIGINT NOT NULL,
    chat_id       BIGINT NOT NULL,
    name          VARCHAR(255),
    creation_date date   NOT NULL,
    creator_id    BIGINT,
    CONSTRAINT pk_group PRIMARY KEY (id)
);

ALTER TABLE "group"
    ADD CONSTRAINT FK_GROUP_ON_CHAT FOREIGN KEY (chat_id) REFERENCES chats (id);

ALTER TABLE "group"
    ADD CONSTRAINT FK_GROUP_ON_CREATOR FOREIGN KEY (creator_id) REFERENCES users (id);