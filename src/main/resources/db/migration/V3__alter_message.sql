UPDATE users
SET password = '$2a$10$tANbclxipO7ZvKrTPATIWObIIemGufEv/hZjpHqQUPz8c0DzApBVG',
    role     = 'USER'
WHERE password IS NULL;

CREATE TABLE IF NOT EXISTS messages_undelivered_to
(
    message_id   BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    CONSTRAINT pk_messages_undelivered_to PRIMARY KEY (message_id, recipient_id)
);


ALTER TABLE messages
    ADD CONSTRAINT FK_MESSAGES_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE messages_undelivered_to
    ADD CONSTRAINT fk_mesundto_on_message FOREIGN KEY (message_id) REFERENCES messages (id);

ALTER TABLE messages_undelivered_to
    ADD CONSTRAINT fk_mesundto_on_user FOREIGN KEY (recipient_id) REFERENCES users (id);