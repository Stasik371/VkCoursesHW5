INSERT INTO products(name_of_product, product_code)
VALUES ('T-shirt Adidas', 1200),
       ('T-shirt Nike', 1201),
       ('Sneakers Nike', 1300),
       ('Sneakers Reebok', 1301),
       ('Jacket Reebok', 1400),
       ('Jacket  Puma', 1401);
INSERT INTO organizations(organizations_name, ind_taxpayer_num, checking_account)
VALUES ('StreetBeat', 123123, 2222),
       ('SportMaster', 234234, 3333),
       ('Decathlon', 345345, 4444),
       ('SportTovary', 456456, 5555);
INSERT INTO invoices(invoice_id, date_of_invoice, organization_num)
VALUES (1, '2022-11-10 15:00:00', 123123),
       (2, '2022-09-10 16:00:00', 234234),
       (3, '2022-08-10 14:00:00', 345345),
       (4, '2022-07-10 12:00:00', 456456);
INSERT INTO invoice_positions(invoice_id, price, amount, product_code)
VALUES (1, 999, 500, 1200),
       (1, 899, 150, 1201),
       (2, 4999, 300, 1300),
       (2, 12999, 400, 1400),
       (3, 11999, 200, 1400),
       (3, 7999, 100, 1401),
       (4, 17999, 20, 1301),
       (4, 1499, 250, 1201);