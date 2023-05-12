ALTER TABLE chats
    ADD group_id BIGINT;

DELETE
FROM chat_members
WHERE chat_id IN (SELECT c.id FROM chats c WHERE c.is_private = FALSE);

DELETE
FROM messages_undelivered_to
WHERE message_id IN (SELECT m.id FROM messages m JOIN chats c on c.id = m.chat_id WHERE c.is_private = FALSE);

DELETE
FROM messages
WHERE chat_id IN (SELECT c.id FROM chats c WHERE c.is_private = FALSE);

DELETE
FROM chats
WHERE is_private = FALSE;