CREATE TABLE organizations
(
    organizations_name VARCHAR NOT NULL,
    ind_taxpayer_num   INTEGER NOT NULL,
    checking_account   INTEGER NOT NULL,
    CONSTRAINT organizations_pk PRIMARY KEY (ind_taxpayer_num)
);

CREATE TABLE products
(
    name_of_product VARCHAR NOT NULL,
    product_code    INTEGER NOT NULL,
    CONSTRAINT products_pk PRIMARY KEY (product_code)
);

CREATE TABLE invoices
(
    invoice_id       SERIAL,
    date_of_invoice  TIMESTAMP NOT NULL,
    organization_num INTEGER   NOT NULL REFERENCES organizations (ind_taxpayer_num) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT invoice_pk PRIMARY KEY (invoice_id)
);
CREATE TABLE invoice_positions
(
    id           SERIAL,
    invoice_id   INTEGER NOT NULL REFERENCES invoices (invoice_id) ON UPDATE CASCADE ON DELETE CASCADE,
    price        INTEGER NOT NULL,
    amount       INTEGER NOT NULL,
    product_code INTEGER NOT NULL REFERENCES products (product_code) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT invoice_positions_pk PRIMARY KEY (id)
);

