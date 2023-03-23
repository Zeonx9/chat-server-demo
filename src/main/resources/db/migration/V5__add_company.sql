CREATE SEQUENCE IF NOT EXISTS company_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE company
(
    id   BIGINT       NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_company PRIMARY KEY (id)
);

INSERT INTO company (id, name)
VALUES (1, 'Test Company');

ALTER TABLE users
    ADD company_id BIGINT NOT NULL default 1;
