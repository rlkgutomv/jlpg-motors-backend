CREATE TABLE products (
    id UUID PRIMARY KEY,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year_model INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    base_currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    plate VARCHAR(10) NOT NULL UNIQUE
);
