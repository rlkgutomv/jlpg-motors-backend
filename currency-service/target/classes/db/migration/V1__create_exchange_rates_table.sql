CREATE TABLE exchange_rates (
    id UUID PRIMARY KEY,
    source_currency VARCHAR(3) NOT NULL,
    target_currency VARCHAR(3) NOT NULL,
    rate DECIMAL(19,6) NOT NULL,
    quotation_date DATE NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_exchange_rate UNIQUE (source_currency, target_currency, quotation_date)
);
