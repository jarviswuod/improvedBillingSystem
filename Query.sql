select * from customers;

ALTER TABLE customers
    ADD COLUMN deleted BOOLEAN DEFAULT FALSE;

ALTER TABLE payments
    ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;


ALTER TABLE invoices
    ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;