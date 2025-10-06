--
-- PostgreSQL database dump
--

-- Dumped from database version 17.6
-- Dumped by pg_dump version 17.6

-- Started on 2025-09-22 01:35:42

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 5 (class 2615 OID 18320)
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

--
-- TOC entry 889 (class 1247 OID 18322)
-- Name: mpaa_rating; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.mpaa_rating AS ENUM (
    'G',
    'PG',
    'PG-13',
    'R',
    'NC-17'
);


--
-- TOC entry 892 (class 1247 OID 18334)
-- Name: year; Type: DOMAIN; Schema: public; Owner: -
--

CREATE DOMAIN public.year AS integer
	CONSTRAINT year_check CHECK (((VALUE >= 1901) AND (VALUE <= 2155)));


--
-- TOC entry 252 (class 1255 OID 18336)
-- Name: _group_concat(text, text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public._group_concat(text, text) RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $_$
SELECT CASE
  WHEN $2 IS NULL THEN $1
  WHEN $1 IS NULL THEN $2
  ELSE $1 || ', ' || $2
END
$_$;


--
-- TOC entry 253 (class 1255 OID 18337)
-- Name: film_in_stock(integer, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.film_in_stock(p_film_id integer, p_store_id integer, OUT p_film_count integer) RETURNS SETOF integer
    LANGUAGE sql
    AS $_$
     SELECT inventory_id
     FROM inventory
     WHERE film_id = $1
     AND store_id = $2
     AND inventory_in_stock(inventory_id);
$_$;


--
-- TOC entry 254 (class 1255 OID 18338)
-- Name: film_not_in_stock(integer, integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.film_not_in_stock(p_film_id integer, p_store_id integer, OUT p_film_count integer) RETURNS SETOF integer
    LANGUAGE sql
    AS $_$
    SELECT inventory_id
    FROM inventory
    WHERE film_id = $1
    AND store_id = $2
    AND NOT inventory_in_stock(inventory_id);
$_$;


--
-- TOC entry 266 (class 1255 OID 18339)
-- Name: get_customer_balance(integer, timestamp without time zone); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.get_customer_balance(p_customer_id integer, p_effective_date timestamp without time zone) RETURNS numeric
    LANGUAGE plpgsql
    AS $$
       --#OK, WE NEED TO CALCULATE THE CURRENT BALANCE GIVEN A CUSTOMER_ID AND A DATE
       --#THAT WE WANT THE BALANCE TO BE EFFECTIVE FOR. THE BALANCE IS:
       --#   1) RENTAL FEES FOR ALL PREVIOUS RENTALS
       --#   2) ONE DOLLAR FOR EVERY DAY THE PREVIOUS RENTALS ARE OVERDUE
       --#   3) IF A FILM IS MORE THAN RENTAL_DURATION * 2 OVERDUE, CHARGE THE REPLACEMENT_COST
       --#   4) SUBTRACT ALL PAYMENTS MADE BEFORE THE DATE SPECIFIED
DECLARE
    v_rentfees DECIMAL(5,2); --#FEES PAID TO RENT THE VIDEOS INITIALLY
    v_overfees INTEGER;      --#LATE FEES FOR PRIOR RENTALS
    v_payments DECIMAL(5,2); --#SUM OF PAYMENTS MADE PREVIOUSLY
BEGIN
    SELECT COALESCE(SUM(film.rental_rate),0) INTO v_rentfees
    FROM film, inventory, rental
    WHERE film.film_id = inventory.film_id
      AND inventory.inventory_id = rental.inventory_id
      AND rental.rental_date <= p_effective_date
      AND rental.customer_id = p_customer_id;

    SELECT COALESCE(SUM(IF((rental.return_date - rental.rental_date) > (film.rental_duration * '1 day'::interval),
        ((rental.return_date - rental.rental_date) - (film.rental_duration * '1 day'::interval)),0)),0) INTO v_overfees
    FROM rental, inventory, film
    WHERE film.film_id = inventory.film_id
      AND inventory.inventory_id = rental.inventory_id
      AND rental.rental_date <= p_effective_date
      AND rental.customer_id = p_customer_id;

    SELECT COALESCE(SUM(payment.amount),0) INTO v_payments
    FROM payment
    WHERE payment.payment_date <= p_effective_date
    AND payment.customer_id = p_customer_id;

    RETURN v_rentfees + v_overfees - v_payments;
END
$$;


--
-- TOC entry 267 (class 1255 OID 18340)
-- Name: inventory_held_by_customer(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.inventory_held_by_customer(p_inventory_id integer) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_customer_id INTEGER;
BEGIN

  SELECT customer_id INTO v_customer_id
  FROM rental
  WHERE return_date IS NULL
  AND inventory_id = p_inventory_id;

  RETURN v_customer_id;
END $$;


--
-- TOC entry 268 (class 1255 OID 18341)
-- Name: inventory_in_stock(integer); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.inventory_in_stock(p_inventory_id integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE
    v_rentals INTEGER;
    v_out     INTEGER;
BEGIN
    -- AN ITEM IS IN-STOCK IF THERE ARE EITHER NO ROWS IN THE rental TABLE
    -- FOR THE ITEM OR ALL ROWS HAVE return_date POPULATED

    SELECT count(*) INTO v_rentals
    FROM rental
    WHERE inventory_id = p_inventory_id;

    IF v_rentals = 0 THEN
      RETURN TRUE;
    END IF;

    SELECT COUNT(rental_id) INTO v_out
    FROM inventory LEFT JOIN rental USING(inventory_id)
    WHERE inventory.inventory_id = p_inventory_id
    AND rental.return_date IS NULL;

    IF v_out > 0 THEN
      RETURN FALSE;
    ELSE
      RETURN TRUE;
    END IF;
END $$;


--
-- TOC entry 269 (class 1255 OID 18342)
-- Name: last_day(timestamp without time zone); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.last_day(timestamp without time zone) RETURNS date
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$
  SELECT CASE
    WHEN EXTRACT(MONTH FROM $1) = 12 THEN
      (((EXTRACT(YEAR FROM $1) + 1) operator(pg_catalog.||) '-01-01')::date - INTERVAL '1 day')::date
    ELSE
      ((EXTRACT(YEAR FROM $1) operator(pg_catalog.||) '-' operator(pg_catalog.||) (EXTRACT(MONTH FROM $1) + 1) operator(pg_catalog.||) '-01')::date - INTERVAL '1 day')::date
    END
$_$;


--
-- TOC entry 270 (class 1255 OID 18343)
-- Name: last_updated(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.last_updated() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.last_update = CURRENT_TIMESTAMP;
    RETURN NEW;
END $$;


--
-- TOC entry 217 (class 1259 OID 18344)
-- Name: customer_customer_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.customer_customer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 218 (class 1259 OID 18345)
-- Name: customer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.customer (
    customer_id integer DEFAULT nextval('public.customer_customer_id_seq'::regclass) NOT NULL,
    store_id smallint NOT NULL,
    first_name character varying(45) NOT NULL,
    last_name character varying(45) NOT NULL,
    email character varying(50),
    address_id smallint NOT NULL,
    activebool boolean DEFAULT true NOT NULL,
    create_date date DEFAULT ('now'::text)::date NOT NULL,
    last_update timestamp without time zone DEFAULT now(),
    active integer
);


--
-- TOC entry 271 (class 1255 OID 18352)
-- Name: rewards_report(integer, numeric); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.rewards_report(min_monthly_purchases integer, min_dollar_amount_purchased numeric) RETURNS SETOF public.customer
    LANGUAGE plpgsql SECURITY DEFINER
    AS $_$
DECLARE
    last_month_start DATE;
    last_month_end DATE;
rr RECORD;
tmpSQL TEXT;
BEGIN

    /* Some sanity checks... */
    IF min_monthly_purchases = 0 THEN
        RAISE EXCEPTION 'Minimum monthly purchases parameter must be > 0';
    END IF;
    IF min_dollar_amount_purchased = 0.00 THEN
        RAISE EXCEPTION 'Minimum monthly dollar amount purchased parameter must be > $0.00';
    END IF;

    last_month_start := CURRENT_DATE - '3 month'::interval;
    last_month_start := to_date((extract(YEAR FROM last_month_start) || '-' || extract(MONTH FROM last_month_start) || '-01'),'YYYY-MM-DD');
    last_month_end := LAST_DAY(last_month_start);

    /*
    Create a temporary storage area for Customer IDs.
    */
    CREATE TEMPORARY TABLE tmpCustomer (customer_id INTEGER NOT NULL PRIMARY KEY);

    /*
    Find all customers meeting the monthly purchase requirements
    */

    tmpSQL := 'INSERT INTO tmpCustomer (customer_id)
        SELECT p.customer_id
        FROM payment AS p
        WHERE DATE(p.payment_date) BETWEEN '||quote_literal(last_month_start) ||' AND '|| quote_literal(last_month_end) || '
        GROUP BY customer_id
        HAVING SUM(p.amount) > '|| min_dollar_amount_purchased || '
        AND COUNT(customer_id) > ' ||min_monthly_purchases ;

    EXECUTE tmpSQL;

    /*
    Output ALL customer information of matching rewardees.
    Customize output as needed.
    */
    FOR rr IN EXECUTE 'SELECT c.* FROM tmpCustomer AS t INNER JOIN customer AS c ON t.customer_id = c.customer_id' LOOP
        RETURN NEXT rr;
    END LOOP;

    /* Clean up */
    tmpSQL := 'DROP TABLE tmpCustomer';
    EXECUTE tmpSQL;

RETURN;
END
$_$;


--
-- TOC entry 963 (class 1255 OID 18353)
-- Name: group_concat(text); Type: AGGREGATE; Schema: public; Owner: -
--

CREATE AGGREGATE public.group_concat(text) (
    SFUNC = public._group_concat,
    STYPE = text
);


--
-- TOC entry 219 (class 1259 OID 18354)
-- Name: actor_actor_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.actor_actor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 220 (class 1259 OID 18355)
-- Name: actor; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.actor (
    actor_id integer DEFAULT nextval('public.actor_actor_id_seq'::regclass) NOT NULL,
    first_name character varying(45) NOT NULL,
    last_name character varying(45) NOT NULL,
    last_update timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 221 (class 1259 OID 18360)
-- Name: category_category_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.category_category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 222 (class 1259 OID 18361)
-- Name: category; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.category (
    category_id integer DEFAULT nextval('public.category_category_id_seq'::regclass) NOT NULL,
    name character varying(25) NOT NULL,
    last_update timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 223 (class 1259 OID 18366)
-- Name: film_film_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.film_film_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 224 (class 1259 OID 18367)
-- Name: film; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.film (
    film_id integer DEFAULT nextval('public.film_film_id_seq'::regclass) NOT NULL,
    title character varying(255) NOT NULL,
    description text,
    release_year public.year,
    language_id smallint NOT NULL,
    rental_duration smallint DEFAULT 3 NOT NULL,
    rental_rate numeric(4,2) DEFAULT 4.99 NOT NULL,
    length smallint,
    replacement_cost numeric(5,2) DEFAULT 19.99 NOT NULL,
    rating public.mpaa_rating DEFAULT 'G'::public.mpaa_rating,
    last_update timestamp without time zone DEFAULT now() NOT NULL,
    special_features text[],
    fulltext tsvector NOT NULL
);


--
-- TOC entry 225 (class 1259 OID 18378)
-- Name: film_actor; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.film_actor (
    actor_id smallint NOT NULL,
    film_id smallint NOT NULL,
    last_update timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 226 (class 1259 OID 18382)
-- Name: film_category; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.film_category (
    film_id smallint NOT NULL,
    category_id smallint NOT NULL,
    last_update timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 227 (class 1259 OID 18386)
-- Name: actor_info; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.actor_info AS
 SELECT a.actor_id,
    a.first_name,
    a.last_name,
    public.group_concat(DISTINCT (((c.name)::text || ': '::text) || ( SELECT public.group_concat((f.title)::text) AS group_concat
           FROM ((public.film f
             JOIN public.film_category fc_1 ON ((f.film_id = fc_1.film_id)))
             JOIN public.film_actor fa_1 ON ((f.film_id = fa_1.film_id)))
          WHERE ((fc_1.category_id = c.category_id) AND (fa_1.actor_id = a.actor_id))
          GROUP BY fa_1.actor_id))) AS film_info
   FROM (((public.actor a
     LEFT JOIN public.film_actor fa ON ((a.actor_id = fa.actor_id)))
     LEFT JOIN public.film_category fc ON ((fa.film_id = fc.film_id)))
     LEFT JOIN public.category c ON ((fc.category_id = c.category_id)))
  GROUP BY a.actor_id, a.first_name, a.last_name;


--
-- TOC entry 228 (class 1259 OID 18391)
-- Name: address_address_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.address_address_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 229 (class 1259 OID 18392)
-- Name: address; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.address (
    address_id integer DEFAULT nextval('public.address_address_id_seq'::regclass) NOT NULL,
    address character varying(50) NOT NULL,
    address2 character varying(50),
    district character varying(20) NOT NULL,
    city_id smallint NOT NULL,
    postal_code character varying(10),
    phone character varying(20) NOT NULL,
    last_update timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 230 (class 1259 OID 18397)
-- Name: city_city_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.city_city_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 231 (class 1259 OID 18398)
-- Name: city; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.city (
    city_id integer DEFAULT nextval('public.city_city_id_seq'::regclass) NOT NULL,
    city character varying(50) NOT NULL,
    country_id smallint NOT NULL,
    last_update timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 232 (class 1259 OID 18403)
-- Name: country_country_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.country_country_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 233 (class 1259 OID 18404)
-- Name: country; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.country (
    country_id integer DEFAULT nextval('public.country_country_id_seq'::regclass) NOT NULL,
    country character varying(50) NOT NULL,
    last_update timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 234 (class 1259 OID 18409)
-- Name: customer_list; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.customer_list AS
 SELECT cu.customer_id AS id,
    (((cu.first_name)::text || ' '::text) || (cu.last_name)::text) AS name,
    a.address,
    a.postal_code AS "zip code",
    a.phone,
    city.city,
    country.country,
        CASE
            WHEN cu.activebool THEN 'active'::text
            ELSE ''::text
        END AS notes,
    cu.store_id AS sid
   FROM (((public.customer cu
     JOIN public.address a ON ((cu.address_id = a.address_id)))
     JOIN public.city ON ((a.city_id = city.city_id)))
     JOIN public.country ON ((city.country_id = country.country_id)));


--
-- TOC entry 235 (class 1259 OID 18414)
-- Name: film_list; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.film_list AS
 SELECT film.film_id AS fid,
    film.title,
    film.description,
    category.name AS category,
    film.rental_rate AS price,
    film.length,
    film.rating,
    public.group_concat((((actor.first_name)::text || ' '::text) || (actor.last_name)::text)) AS actors
   FROM ((((public.category
     LEFT JOIN public.film_category ON ((category.category_id = film_category.category_id)))
     LEFT JOIN public.film ON ((film_category.film_id = film.film_id)))
     JOIN public.film_actor ON ((film.film_id = film_actor.film_id)))
     JOIN public.actor ON ((film_actor.actor_id = actor.actor_id)))
  GROUP BY film.film_id, film.title, film.description, category.name, film.rental_rate, film.length, film.rating;


--
-- TOC entry 236 (class 1259 OID 18419)
-- Name: inventory_inventory_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.inventory_inventory_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 237 (class 1259 OID 18420)
-- Name: inventory; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.inventory (
    inventory_id integer DEFAULT nextval('public.inventory_inventory_id_seq'::regclass) NOT NULL,
    film_id smallint NOT NULL,
    store_id smallint NOT NULL,
    last_update timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 238 (class 1259 OID 18425)
-- Name: language_language_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.language_language_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 239 (class 1259 OID 18426)
-- Name: language; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.language (
    language_id integer DEFAULT nextval('public.language_language_id_seq'::regclass) NOT NULL,
    name character(20) NOT NULL,
    last_update timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 240 (class 1259 OID 18431)
-- Name: nicer_but_slower_film_list; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.nicer_but_slower_film_list AS
 SELECT film.film_id AS fid,
    film.title,
    film.description,
    category.name AS category,
    film.rental_rate AS price,
    film.length,
    film.rating,
    public.group_concat((((upper("substring"((actor.first_name)::text, 1, 1)) || lower("substring"((actor.first_name)::text, 2))) || upper("substring"((actor.last_name)::text, 1, 1))) || lower("substring"((actor.last_name)::text, 2)))) AS actors
   FROM ((((public.category
     LEFT JOIN public.film_category ON ((category.category_id = film_category.category_id)))
     LEFT JOIN public.film ON ((film_category.film_id = film.film_id)))
     JOIN public.film_actor ON ((film.film_id = film_actor.film_id)))
     JOIN public.actor ON ((film_actor.actor_id = actor.actor_id)))
  GROUP BY film.film_id, film.title, film.description, category.name, film.rental_rate, film.length, film.rating;


--
-- TOC entry 241 (class 1259 OID 18436)
-- Name: payment_payment_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.payment_payment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 242 (class 1259 OID 18437)
-- Name: payment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.payment (
    payment_id integer DEFAULT nextval('public.payment_payment_id_seq'::regclass) NOT NULL,
    customer_id smallint NOT NULL,
    staff_id smallint NOT NULL,
    rental_id integer NOT NULL,
    amount numeric(5,2) NOT NULL,
    payment_date timestamp without time zone NOT NULL
);


--
-- TOC entry 243 (class 1259 OID 18441)
-- Name: rental_rental_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.rental_rental_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 244 (class 1259 OID 18442)
-- Name: rental; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.rental (
    rental_id integer DEFAULT nextval('public.rental_rental_id_seq'::regclass) NOT NULL,
    rental_date timestamp without time zone NOT NULL,
    inventory_id integer NOT NULL,
    customer_id smallint NOT NULL,
    return_date timestamp without time zone,
    staff_id smallint NOT NULL,
    last_update timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 245 (class 1259 OID 18447)
-- Name: sales_by_film_category; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.sales_by_film_category AS
 SELECT c.name AS category,
    sum(p.amount) AS total_sales
   FROM (((((public.payment p
     JOIN public.rental r ON ((p.rental_id = r.rental_id)))
     JOIN public.inventory i ON ((r.inventory_id = i.inventory_id)))
     JOIN public.film f ON ((i.film_id = f.film_id)))
     JOIN public.film_category fc ON ((f.film_id = fc.film_id)))
     JOIN public.category c ON ((fc.category_id = c.category_id)))
  GROUP BY c.name
  ORDER BY (sum(p.amount)) DESC;


--
-- TOC entry 246 (class 1259 OID 18452)
-- Name: staff_staff_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.staff_staff_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 247 (class 1259 OID 18453)
-- Name: staff; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.staff (
    staff_id integer DEFAULT nextval('public.staff_staff_id_seq'::regclass) NOT NULL,
    first_name character varying(45) NOT NULL,
    last_name character varying(45) NOT NULL,
    address_id smallint NOT NULL,
    email character varying(50),
    store_id smallint NOT NULL,
    active boolean DEFAULT true NOT NULL,
    username character varying(16) NOT NULL,
    password character varying(40),
    last_update timestamp without time zone DEFAULT now() NOT NULL,
    picture bytea
);


--
-- TOC entry 248 (class 1259 OID 18461)
-- Name: store_store_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.store_store_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 249 (class 1259 OID 18462)
-- Name: store; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.store (
    store_id integer DEFAULT nextval('public.store_store_id_seq'::regclass) NOT NULL,
    manager_staff_id smallint NOT NULL,
    address_id smallint NOT NULL,
    last_update timestamp without time zone DEFAULT now() NOT NULL
);


--
-- TOC entry 250 (class 1259 OID 18467)
-- Name: sales_by_store; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.sales_by_store AS
 SELECT (((c.city)::text || ','::text) || (cy.country)::text) AS store,
    (((m.first_name)::text || ' '::text) || (m.last_name)::text) AS manager,
    sum(p.amount) AS total_sales
   FROM (((((((public.payment p
     JOIN public.rental r ON ((p.rental_id = r.rental_id)))
     JOIN public.inventory i ON ((r.inventory_id = i.inventory_id)))
     JOIN public.store s ON ((i.store_id = s.store_id)))
     JOIN public.address a ON ((s.address_id = a.address_id)))
     JOIN public.city c ON ((a.city_id = c.city_id)))
     JOIN public.country cy ON ((c.country_id = cy.country_id)))
     JOIN public.staff m ON ((s.manager_staff_id = m.staff_id)))
  GROUP BY cy.country, c.city, s.store_id, m.first_name, m.last_name
  ORDER BY cy.country, c.city;


--
-- TOC entry 251 (class 1259 OID 18472)
-- Name: staff_list; Type: VIEW; Schema: public; Owner: -
--

CREATE VIEW public.staff_list AS
 SELECT s.staff_id AS id,
    (((s.first_name)::text || ' '::text) || (s.last_name)::text) AS name,
    a.address,
    a.postal_code AS "zip code",
    a.phone,
    city.city,
    country.country,
    s.store_id AS sid
   FROM (((public.staff s
     JOIN public.address a ON ((s.address_id = a.address_id)))
     JOIN public.city ON ((a.city_id = city.city_id)))
     JOIN public.country ON ((city.country_id = country.country_id)));


--
-- TOC entry 4895 (class 2606 OID 18478)
-- Name: actor actor_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.actor
    ADD CONSTRAINT actor_pkey PRIMARY KEY (actor_id);


--
-- TOC entry 4910 (class 2606 OID 18480)
-- Name: address address_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT address_pkey PRIMARY KEY (address_id);


--
-- TOC entry 4898 (class 2606 OID 18482)
-- Name: category category_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.category
    ADD CONSTRAINT category_pkey PRIMARY KEY (category_id);


--
-- TOC entry 4913 (class 2606 OID 18484)
-- Name: city city_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.city
    ADD CONSTRAINT city_pkey PRIMARY KEY (city_id);


--
-- TOC entry 4916 (class 2606 OID 18486)
-- Name: country country_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.country
    ADD CONSTRAINT country_pkey PRIMARY KEY (country_id);


--
-- TOC entry 4890 (class 2606 OID 18488)
-- Name: customer customer_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (customer_id);


--
-- TOC entry 4905 (class 2606 OID 18490)
-- Name: film_actor film_actor_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.film_actor
    ADD CONSTRAINT film_actor_pkey PRIMARY KEY (actor_id, film_id);


--
-- TOC entry 4908 (class 2606 OID 18492)
-- Name: film_category film_category_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.film_category
    ADD CONSTRAINT film_category_pkey PRIMARY KEY (film_id, category_id);


--
-- TOC entry 4901 (class 2606 OID 18494)
-- Name: film film_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.film
    ADD CONSTRAINT film_pkey PRIMARY KEY (film_id);


--
-- TOC entry 4919 (class 2606 OID 18496)
-- Name: inventory inventory_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.inventory
    ADD CONSTRAINT inventory_pkey PRIMARY KEY (inventory_id);


--
-- TOC entry 4921 (class 2606 OID 18498)
-- Name: language language_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.language
    ADD CONSTRAINT language_pkey PRIMARY KEY (language_id);


--
-- TOC entry 4926 (class 2606 OID 18500)
-- Name: payment payment_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_pkey PRIMARY KEY (payment_id);


--
-- TOC entry 4930 (class 2606 OID 18502)
-- Name: rental rental_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rental
    ADD CONSTRAINT rental_pkey PRIMARY KEY (rental_id);


--
-- TOC entry 4932 (class 2606 OID 18504)
-- Name: staff staff_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT staff_pkey PRIMARY KEY (staff_id);


--
-- TOC entry 4935 (class 2606 OID 18506)
-- Name: store store_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.store
    ADD CONSTRAINT store_pkey PRIMARY KEY (store_id);


--
-- TOC entry 4899 (class 1259 OID 18507)
-- Name: film_fulltext_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX film_fulltext_idx ON public.film USING gist (fulltext);


--
-- TOC entry 4896 (class 1259 OID 18508)
-- Name: idx_actor_last_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_actor_last_name ON public.actor USING btree (last_name);


--
-- TOC entry 4891 (class 1259 OID 18509)
-- Name: idx_fk_address_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fk_address_id ON public.customer USING btree (address_id);


--
-- TOC entry 4911 (class 1259 OID 18510)
-- Name: idx_fk_city_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fk_city_id ON public.address USING btree (city_id);


--
-- TOC entry 4914 (class 1259 OID 18511)
-- Name: idx_fk_country_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fk_country_id ON public.city USING btree (country_id);


--
-- TOC entry 4922 (class 1259 OID 18512)
-- Name: idx_fk_customer_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fk_customer_id ON public.payment USING btree (customer_id);


--
-- TOC entry 4906 (class 1259 OID 18513)
-- Name: idx_fk_film_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fk_film_id ON public.film_actor USING btree (film_id);


--
-- TOC entry 4927 (class 1259 OID 18514)
-- Name: idx_fk_inventory_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fk_inventory_id ON public.rental USING btree (inventory_id);


--
-- TOC entry 4902 (class 1259 OID 18515)
-- Name: idx_fk_language_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fk_language_id ON public.film USING btree (language_id);


--
-- TOC entry 4923 (class 1259 OID 18516)
-- Name: idx_fk_rental_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fk_rental_id ON public.payment USING btree (rental_id);


--
-- TOC entry 4924 (class 1259 OID 18517)
-- Name: idx_fk_staff_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fk_staff_id ON public.payment USING btree (staff_id);


--
-- TOC entry 4892 (class 1259 OID 18518)
-- Name: idx_fk_store_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_fk_store_id ON public.customer USING btree (store_id);


--
-- TOC entry 4893 (class 1259 OID 18519)
-- Name: idx_last_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_last_name ON public.customer USING btree (last_name);


--
-- TOC entry 4917 (class 1259 OID 18520)
-- Name: idx_store_id_film_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_store_id_film_id ON public.inventory USING btree (store_id, film_id);


--
-- TOC entry 4903 (class 1259 OID 18521)
-- Name: idx_title; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_title ON public.film USING btree (title);


--
-- TOC entry 4933 (class 1259 OID 18522)
-- Name: idx_unq_manager_staff_id; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_unq_manager_staff_id ON public.store USING btree (manager_staff_id);


--
-- TOC entry 4928 (class 1259 OID 18523)
-- Name: idx_unq_rental_rental_date_inventory_id_customer_id; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_unq_rental_rental_date_inventory_id_customer_id ON public.rental USING btree (rental_date, inventory_id, customer_id);


--
-- TOC entry 4957 (class 2620 OID 18524)
-- Name: film film_fulltext_trigger; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER film_fulltext_trigger BEFORE INSERT OR UPDATE ON public.film FOR EACH ROW EXECUTE FUNCTION tsvector_update_trigger('fulltext', 'pg_catalog.english', 'title', 'description');


--
-- TOC entry 4955 (class 2620 OID 18525)
-- Name: actor last_updated; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER last_updated BEFORE UPDATE ON public.actor FOR EACH ROW EXECUTE FUNCTION public.last_updated();


--
-- TOC entry 4961 (class 2620 OID 18526)
-- Name: address last_updated; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER last_updated BEFORE UPDATE ON public.address FOR EACH ROW EXECUTE FUNCTION public.last_updated();


--
-- TOC entry 4956 (class 2620 OID 18527)
-- Name: category last_updated; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER last_updated BEFORE UPDATE ON public.category FOR EACH ROW EXECUTE FUNCTION public.last_updated();


--
-- TOC entry 4962 (class 2620 OID 18528)
-- Name: city last_updated; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER last_updated BEFORE UPDATE ON public.city FOR EACH ROW EXECUTE FUNCTION public.last_updated();


--
-- TOC entry 4963 (class 2620 OID 18529)
-- Name: country last_updated; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER last_updated BEFORE UPDATE ON public.country FOR EACH ROW EXECUTE FUNCTION public.last_updated();


--
-- TOC entry 4954 (class 2620 OID 18530)
-- Name: customer last_updated; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER last_updated BEFORE UPDATE ON public.customer FOR EACH ROW EXECUTE FUNCTION public.last_updated();


--
-- TOC entry 4958 (class 2620 OID 18531)
-- Name: film last_updated; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER last_updated BEFORE UPDATE ON public.film FOR EACH ROW EXECUTE FUNCTION public.last_updated();


--
-- TOC entry 4959 (class 2620 OID 18532)
-- Name: film_actor last_updated; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER last_updated BEFORE UPDATE ON public.film_actor FOR EACH ROW EXECUTE FUNCTION public.last_updated();


--
-- TOC entry 4960 (class 2620 OID 18533)
-- Name: film_category last_updated; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER last_updated BEFORE UPDATE ON public.film_category FOR EACH ROW EXECUTE FUNCTION public.last_updated();


--
-- TOC entry 4964 (class 2620 OID 18534)
-- Name: inventory last_updated; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER last_updated BEFORE UPDATE ON public.inventory FOR EACH ROW EXECUTE FUNCTION public.last_updated();


--
-- TOC entry 4965 (class 2620 OID 18535)
-- Name: language last_updated; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER last_updated BEFORE UPDATE ON public.language FOR EACH ROW EXECUTE FUNCTION public.last_updated();


--
-- TOC entry 4966 (class 2620 OID 18536)
-- Name: rental last_updated; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER last_updated BEFORE UPDATE ON public.rental FOR EACH ROW EXECUTE FUNCTION public.last_updated();


--
-- TOC entry 4967 (class 2620 OID 18537)
-- Name: staff last_updated; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER last_updated BEFORE UPDATE ON public.staff FOR EACH ROW EXECUTE FUNCTION public.last_updated();


--
-- TOC entry 4968 (class 2620 OID 18538)
-- Name: store last_updated; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER last_updated BEFORE UPDATE ON public.store FOR EACH ROW EXECUTE FUNCTION public.last_updated();


--
-- TOC entry 4936 (class 2606 OID 18539)
-- Name: customer customer_address_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_address_id_fkey FOREIGN KEY (address_id) REFERENCES public.address(address_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4938 (class 2606 OID 18544)
-- Name: film_actor film_actor_actor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.film_actor
    ADD CONSTRAINT film_actor_actor_id_fkey FOREIGN KEY (actor_id) REFERENCES public.actor(actor_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4939 (class 2606 OID 18549)
-- Name: film_actor film_actor_film_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.film_actor
    ADD CONSTRAINT film_actor_film_id_fkey FOREIGN KEY (film_id) REFERENCES public.film(film_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4940 (class 2606 OID 18554)
-- Name: film_category film_category_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.film_category
    ADD CONSTRAINT film_category_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.category(category_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4941 (class 2606 OID 18559)
-- Name: film_category film_category_film_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.film_category
    ADD CONSTRAINT film_category_film_id_fkey FOREIGN KEY (film_id) REFERENCES public.film(film_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4937 (class 2606 OID 18564)
-- Name: film film_language_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.film
    ADD CONSTRAINT film_language_id_fkey FOREIGN KEY (language_id) REFERENCES public.language(language_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4942 (class 2606 OID 18569)
-- Name: address fk_address_city; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.address
    ADD CONSTRAINT fk_address_city FOREIGN KEY (city_id) REFERENCES public.city(city_id);


--
-- TOC entry 4943 (class 2606 OID 18574)
-- Name: city fk_city; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.city
    ADD CONSTRAINT fk_city FOREIGN KEY (country_id) REFERENCES public.country(country_id);


--
-- TOC entry 4944 (class 2606 OID 18579)
-- Name: inventory inventory_film_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.inventory
    ADD CONSTRAINT inventory_film_id_fkey FOREIGN KEY (film_id) REFERENCES public.film(film_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4945 (class 2606 OID 18584)
-- Name: payment payment_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES public.customer(customer_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4946 (class 2606 OID 18589)
-- Name: payment payment_rental_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_rental_id_fkey FOREIGN KEY (rental_id) REFERENCES public.rental(rental_id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- TOC entry 4947 (class 2606 OID 18594)
-- Name: payment payment_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.payment
    ADD CONSTRAINT payment_staff_id_fkey FOREIGN KEY (staff_id) REFERENCES public.staff(staff_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4948 (class 2606 OID 18599)
-- Name: rental rental_customer_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rental
    ADD CONSTRAINT rental_customer_id_fkey FOREIGN KEY (customer_id) REFERENCES public.customer(customer_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4949 (class 2606 OID 18604)
-- Name: rental rental_inventory_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rental
    ADD CONSTRAINT rental_inventory_id_fkey FOREIGN KEY (inventory_id) REFERENCES public.inventory(inventory_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4950 (class 2606 OID 18609)
-- Name: rental rental_staff_id_key; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.rental
    ADD CONSTRAINT rental_staff_id_key FOREIGN KEY (staff_id) REFERENCES public.staff(staff_id);


--
-- TOC entry 4951 (class 2606 OID 18614)
-- Name: staff staff_address_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.staff
    ADD CONSTRAINT staff_address_id_fkey FOREIGN KEY (address_id) REFERENCES public.address(address_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4952 (class 2606 OID 18619)
-- Name: store store_address_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.store
    ADD CONSTRAINT store_address_id_fkey FOREIGN KEY (address_id) REFERENCES public.address(address_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 4953 (class 2606 OID 18624)
-- Name: store store_manager_staff_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.store
    ADD CONSTRAINT store_manager_staff_id_fkey FOREIGN KEY (manager_staff_id) REFERENCES public.staff(staff_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- TOC entry 5126 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: -
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;


-- Completed on 2025-09-22 01:35:42

--
-- PostgreSQL database dump complete
--

