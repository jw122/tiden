DROP TABLE IF EXISTS public.payment;

CREATE TABLE IF NOT EXISTS public.payment
(
    "id"                  serial PRIMARY KEY,
    "amount"              decimal NOT NULL,
    "description"         varchar(255),
    "email"               varchar(255),
    "phone_number"        varchar(255),
    "verification_method" varchar(255),
    "cvv"                 varchar(8000),
    "source_id"           varchar(255),
    "source_type"         varchar(255)
);

GRANT ALL PRIVILEGES ON TABLE public.payment TO flagship;
