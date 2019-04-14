CREATE TABLE IF NOT EXISTS PhotoItem (
    id int8 not null,
    category varchar(255),
    name varchar(255),
    primary key (id)
);

CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START 1;