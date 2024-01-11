INSERT INTO unread_counter (count, user_id, chat_id)
SELECT 0, user_id, chat_id
FROM chat_members
