-- merchant table
DROP TABLE IF EXISTS public.merchant CASCADE;

CREATE TABLE public.merchant
(
    "id"               serial PRIMARY KEY,
    name               varchar(255) NOT NULL,
    blockchain_address varchar(255)
);

-- TODO: remove test merchant after we have merchant flow
INSERT INTO public.merchant(name) VALUES ('test');

GRANT ALL PRIVILEGES ON TABLE public.merchant TO flagship;

-- payment table
DROP TABLE IF EXISTS public.payment;

CREATE TABLE IF NOT EXISTS public.payment
(
    "id"                 serial PRIMARY KEY,
    payment_id           varchar(255),
    amount               decimal   NOT NULL,
    description          varchar(255),
    email                varchar(255),
    phone_number         varchar(255),
    verification_method  varchar(255),
    -- card details
    source_id            varchar(255),
    source_type          varchar(255),
    cvv                  varchar(8000),
    expiration_month     int,
    expiration_year      int,
    -- billing details
    -- name must be first + last
    name                 varchar(255),
    address              text,
    city                 varchar(255),
    district             varchar(255),
    postal_code          varchar(255),
    -- must be valid ISO 31660-2 country code
    country              varchar(255),
    -- tiden's id payment processor id
    platform_merchant_id varchar(255),
    platform_wallet_id   varchar(255),
    -- status of the payment
    status               varchar(255),
    fee_amount           varchar(255),
    fee_currency         varchar(255),
    tracking_ref         varchar(255),
    risk_decision_reason varchar(255),
    risk_decision        varchar(255),
    create_date          timestamp,
    update_date          timestamp,
    currency             varchar(255),
    merchant_id          bigint    NOT NULL,
    CONSTRAINT FK_payment_merchant_id FOREIGN KEY (merchant_id) REFERENCES merchant ("id")
);

CREATE INDEX index_fk_payment_merchant_id ON "public".payment
    (
     merchant_id
        );

CREATE INDEX index_payment_status ON "public".payment
    (
     status
        );

GRANT ALL PRIVILEGES ON TABLE public.payment TO flagship;

-- merchant_balance table
DROP TABLE IF EXISTS public.merchant_balance;
CREATE TABLE public.merchant_balance
(
    "id"        bigint  NOT NULL,
    amount      decimal NOT NULL,
    merchant_id bigint  NOT NULL,
    CONSTRAINT PK_merchant_balance PRIMARY KEY ("id"),
    CONSTRAINT FK_merhant_balance_merchant FOREIGN KEY (merchant_id) REFERENCES merchant ("id")
);

INSERT INTO public.merchant_balance(id, amount, merchant_id) VALUES (1, 0, 1);

CREATE INDEX index_fk_merchant_balance_merchant_id ON public.merchant_balance
    (
     merchant_id
        );
