--MUST be one statement per line!!!  And MUST be valid for hsql if that is what you are using.
SET DATABASE SQL SYNTAX ORA TRUE;
CREATE TABLE orders ( order_id decimal(22) PRIMARY KEY, customer_id decimal(22), total decimal(7,2), result varchar(50) );
CREATE SEQUENCE orders_seq START WITH 1000 INCREMENT BY 1;
