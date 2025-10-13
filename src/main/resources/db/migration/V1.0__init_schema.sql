CREATE TYPE mpaa_rating AS ENUM
    ('G', 'PG', 'PG-13', 'R', 'NC-17');

CREATE OR REPLACE FUNCTION _group_concat(
    text,
    text)
    RETURNS text
    LANGUAGE 'sql'
    COST 100
    IMMUTABLE PARALLEL UNSAFE
AS $BODY$
SELECT CASE
           WHEN $2 IS NULL THEN $1
           WHEN $1 IS NULL THEN $2
           ELSE $1 || ', ' || $2
           END
$BODY$;

CREATE OR REPLACE AGGREGATE group_concat(text) (
    SFUNC = _group_concat,
    STYPE = text ,
    FINALFUNC_MODIFY = READ_ONLY,
    MFINALFUNC_MODIFY = READ_ONLY
    );

CREATE DOMAIN year
    AS integer;

ALTER DOMAIN year
    ADD CONSTRAINT year_check CHECK (VALUE >= 1901 AND VALUE <= 2155);

CREATE SEQUENCE IF NOT EXISTS actor_actor_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS address_address_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS category_category_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS city_city_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS country_country_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS customer_customer_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS film_film_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS inventory_inventory_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS language_language_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS payment_payment_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS rental_rental_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS staff_staff_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS store_store_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE actor
(
    actor_id    INTEGER                     DEFAULT nextval('actor_actor_id_seq') NOT NULL,
    first_name  VARCHAR(45)                                                       NOT NULL,
    last_name   VARCHAR(45)                                                       NOT NULL,
    last_update TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()                         NOT NULL,
    CONSTRAINT actor_pkey PRIMARY KEY (actor_id)
);

CREATE TABLE address
(
    address_id  INTEGER                     DEFAULT nextval('address_address_id_seq') NOT NULL,
    address     VARCHAR(50)                                                           NOT NULL,
    address2    VARCHAR(50),
    district    VARCHAR(20)                                                           NOT NULL,
    city_id     SMALLINT                                                              NOT NULL,
    postal_code VARCHAR(10),
    phone       VARCHAR(20)                                                           NOT NULL,
    last_update TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()                             NOT NULL,
    CONSTRAINT address_pkey PRIMARY KEY (address_id)
);

CREATE TABLE category
(
    category_id INTEGER                     DEFAULT nextval('category_category_id_seq') NOT NULL,
    name        VARCHAR(25)                                                             NOT NULL,
    last_update TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()                               NOT NULL,
    CONSTRAINT category_pkey PRIMARY KEY (category_id)
);

CREATE TABLE city
(
    city_id     INTEGER                     DEFAULT nextval('city_city_id_seq') NOT NULL,
    city        VARCHAR(50)                                                     NOT NULL,
    country_id  SMALLINT                                                        NOT NULL,
    last_update TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()                       NOT NULL,
    CONSTRAINT city_pkey PRIMARY KEY (city_id)
);

CREATE TABLE country
(
    country_id  INTEGER                     DEFAULT nextval('country_country_id_seq') NOT NULL,
    country     VARCHAR(50)                                                           NOT NULL,
    last_update TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()                             NOT NULL,
    CONSTRAINT country_pkey PRIMARY KEY (country_id)
);

CREATE TABLE customer
(
    customer_id INTEGER                     DEFAULT nextval('customer_customer_id_seq') NOT NULL,
    store_id    SMALLINT                                                                NOT NULL,
    first_name  VARCHAR(45)                                                             NOT NULL,
    last_name   VARCHAR(45)                                                             NOT NULL,
    email       VARCHAR(50),
    address_id  SMALLINT                                                                NOT NULL,
    activebool  BOOLEAN                     DEFAULT TRUE                                NOT NULL,
    create_date date                        DEFAULT ('now')                             NOT NULL,
    last_update TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    active      INTEGER,
    CONSTRAINT customer_pkey PRIMARY KEY (customer_id)
);

CREATE TABLE film
(
    film_id          INTEGER                     DEFAULT nextval('film_film_id_seq') NOT NULL,
    title            VARCHAR(255)                                                    NOT NULL,
    description      TEXT,
    release_year     YEAR,
    language_id      SMALLINT                                                        NOT NULL,
    rental_duration  SMALLINT                    DEFAULT 3                           NOT NULL,
    rental_rate      numeric(4, 2)               DEFAULT 4.99                        NOT NULL,
    length           SMALLINT,
    replacement_cost numeric(5, 2)               DEFAULT 19.99                       NOT NULL,
    rating           MPAA_RATING                 DEFAULT 'G',
    last_update      TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()                       NOT NULL,
    special_features TEXT[],
    fulltext         TSVECTOR                                                        NOT NULL,
    CONSTRAINT film_pkey PRIMARY KEY (film_id)
);

CREATE TABLE film_actor
(
    actor_id    SMALLINT                                  NOT NULL,
    film_id     SMALLINT                                  NOT NULL,
    last_update TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
    CONSTRAINT film_actor_pkey PRIMARY KEY (actor_id, film_id)
);

CREATE TABLE film_category
(
    film_id     SMALLINT                                  NOT NULL,
    category_id SMALLINT                                  NOT NULL,
    last_update TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
    CONSTRAINT film_category_pkey PRIMARY KEY (film_id, category_id)
);

CREATE TABLE inventory
(
    inventory_id INTEGER                     DEFAULT nextval('inventory_inventory_id_seq') NOT NULL,
    film_id      SMALLINT                                                                  NOT NULL,
    store_id     SMALLINT                                                                  NOT NULL,
    last_update  TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()                                 NOT NULL,
    CONSTRAINT inventory_pkey PRIMARY KEY (inventory_id)
);

CREATE TABLE language
(
    language_id INTEGER                     DEFAULT nextval('language_language_id_seq') NOT NULL,
    name        CHAR(20)                                                                NOT NULL,
    last_update TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()                               NOT NULL,
    CONSTRAINT language_pkey PRIMARY KEY (language_id)
);

CREATE TABLE payment
(
    payment_id   INTEGER DEFAULT nextval('payment_payment_id_seq') NOT NULL,
    customer_id  SMALLINT                                          NOT NULL,
    staff_id     SMALLINT                                          NOT NULL,
    rental_id    INTEGER                                           NOT NULL,
    amount       numeric(5, 2)                                     NOT NULL,
    payment_date TIMESTAMP WITHOUT TIME ZONE                       NOT NULL,
    CONSTRAINT payment_pkey PRIMARY KEY (payment_id)
);

CREATE TABLE rental
(
    rental_id    INTEGER                     DEFAULT nextval('rental_rental_id_seq') NOT NULL,
    rental_date  TIMESTAMP WITHOUT TIME ZONE                                         NOT NULL,
    inventory_id INTEGER                                                             NOT NULL,
    customer_id  SMALLINT                                                            NOT NULL,
    return_date  TIMESTAMP WITHOUT TIME ZONE,
    staff_id     SMALLINT                                                            NOT NULL,
    last_update  TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()                           NOT NULL,
    CONSTRAINT rental_pkey PRIMARY KEY (rental_id)
);

CREATE TABLE staff
(
    staff_id    INTEGER                     DEFAULT nextval('staff_staff_id_seq') NOT NULL,
    first_name  VARCHAR(45)                                                       NOT NULL,
    last_name   VARCHAR(45)                                                       NOT NULL,
    address_id  SMALLINT                                                          NOT NULL,
    email       VARCHAR(50),
    store_id    SMALLINT                                                          NOT NULL,
    active      BOOLEAN                     DEFAULT TRUE                          NOT NULL,
    username    VARCHAR(16)                                                       NOT NULL,
    password    VARCHAR(40),
    last_update TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()                         NOT NULL,
    picture     BYTEA,
    CONSTRAINT staff_pkey PRIMARY KEY (staff_id)
);

CREATE TABLE store
(
    store_id         INTEGER                     DEFAULT nextval('store_store_id_seq') NOT NULL,
    manager_staff_id SMALLINT                                                          NOT NULL,
    address_id       SMALLINT                                                          NOT NULL,
    last_update      TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()                         NOT NULL,
    CONSTRAINT store_pkey PRIMARY KEY (store_id)
);

CREATE INDEX film_fulltext_idx ON film (fulltext);

CREATE INDEX idx_actor_last_name ON actor (last_name);

CREATE INDEX idx_fk_store_id ON customer (store_id);

CREATE INDEX idx_last_name ON customer (last_name);

CREATE INDEX idx_store_id_film_id ON inventory (store_id, film_id);

CREATE INDEX idx_title ON film (title);

CREATE UNIQUE INDEX idx_unq_rental_rental_date_inventory_id_customer_id ON rental (rental_date, inventory_id, customer_id);

ALTER TABLE customer
    ADD CONSTRAINT customer_address_id_fkey FOREIGN KEY (address_id) REFERENCES address (address_id) ON DELETE RESTRICT;

CREATE INDEX idx_fk_address_id ON customer (address_id);

ALTER TABLE film_actor
    ADD CONSTRAINT film_actor_actor_id_fkey FOREIGN KEY (actor_id) REFERENCES actor (actor_id) ON DELETE RESTRICT;

ALTER TABLE film_actor
    ADD CONSTRAINT film_actor_film_id_fkey FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE RESTRICT;

CREATE INDEX idx_fk_film_id ON film_actor (film_id);

ALTER TABLE film_category
    ADD CONSTRAINT film_category_category_id_fkey FOREIGN KEY (category_id) REFERENCES category (category_id) ON DELETE RESTRICT;

ALTER TABLE film_category
    ADD CONSTRAINT film_category_film_id_fkey FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE RESTRICT;

ALTER TABLE film
    ADD CONSTRAINT film_language_id_fkey FOREIGN KEY (language_id) REFERENCES language (language_id) ON DELETE RESTRICT;

CREATE INDEX idx_fk_language_id ON film (language_id);

ALTER TABLE address
    ADD CONSTRAINT fk_address_city FOREIGN KEY (city_id) REFERENCES city (city_id) ON DELETE NO ACTION;

CREATE INDEX idx_fk_city_id ON address (city_id);

ALTER TABLE city
    ADD CONSTRAINT fk_city FOREIGN KEY (country_id) REFERENCES country (country_id) ON DELETE NO ACTION;

CREATE INDEX idx_fk_country_id ON city (country_id);

ALTER TABLE inventory
    ADD CONSTRAINT inventory_film_id_fkey FOREIGN KEY (film_id) REFERENCES film (film_id) ON DELETE RESTRICT;

ALTER TABLE payment
    ADD CONSTRAINT payment_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE RESTRICT;

CREATE INDEX idx_fk_customer_id ON payment (customer_id);

ALTER TABLE payment
    ADD CONSTRAINT payment_rental_id_fkey FOREIGN KEY (rental_id) REFERENCES rental (rental_id) ON DELETE SET NULL;

CREATE INDEX idx_fk_rental_id ON payment (rental_id);

ALTER TABLE payment
    ADD CONSTRAINT payment_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES staff (staff_id) ON DELETE RESTRICT;

CREATE INDEX idx_fk_staff_id ON payment (staff_id);

ALTER TABLE rental
    ADD CONSTRAINT rental_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES customer (customer_id) ON DELETE RESTRICT;

ALTER TABLE rental
    ADD CONSTRAINT rental_inventory_id_fkey FOREIGN KEY (inventory_id) REFERENCES inventory (inventory_id) ON DELETE RESTRICT;

CREATE INDEX idx_fk_inventory_id ON rental (inventory_id);

ALTER TABLE rental
    ADD CONSTRAINT rental_staff_id_key FOREIGN KEY (staff_id) REFERENCES staff (staff_id) ON DELETE NO ACTION;

ALTER TABLE staff
    ADD CONSTRAINT staff_address_id_fkey FOREIGN KEY (address_id) REFERENCES address (address_id) ON DELETE RESTRICT;

ALTER TABLE store
    ADD CONSTRAINT store_address_id_fkey FOREIGN KEY (address_id) REFERENCES address (address_id) ON DELETE RESTRICT;

ALTER TABLE store
    ADD CONSTRAINT store_manager_staff_id_fkey FOREIGN KEY (manager_staff_id) REFERENCES staff (staff_id) ON DELETE RESTRICT;

CREATE UNIQUE INDEX idx_unq_manager_staff_id ON store (manager_staff_id);
