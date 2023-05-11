INSERT INTO users (id, name, password, company_id, role)
VALUES (nextval('user_sequence'), 'ADMIN_DEFAULT', '$2a$10$y6.XQNPAwoDJ8moi/pUJ1OuBK01XBqCNAKVLRwuds5amkIrivu6Uq', 1, 'ADMIN');