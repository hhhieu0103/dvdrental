SELECT setval('actor_actor_id_seq', (SELECT MAX(actor_id) FROM actor) + 1);

SELECT setval('address_address_id_seq', (SELECT MAX(address_id) FROM address) + 1);

SELECT setval('category_category_id_seq', (SELECT MAX(category_id) FROM category) + 1);

SELECT setval('city_city_id_seq', (SELECT MAX(city_id) FROM city) + 1);

SELECT setval('country_country_id_seq', (SELECT MAX(country_id) FROM country) + 1);

SELECT setval('customer_customer_id_seq', (SELECT MAX(customer_id) FROM customer) + 1);

SELECT setval('film_film_id_seq', (SELECT MAX(film_id) FROM film) + 1);

SELECT setval('inventory_inventory_id_seq', (SELECT MAX(inventory_id) FROM inventory) + 1);

SELECT setval('language_language_id_seq', (SELECT MAX(language_id) FROM language) + 1);

SELECT setval('payment_payment_id_seq', (SELECT MAX(payment_id) FROM payment) + 1);

SELECT setval('rental_rental_id_seq', (SELECT MAX(rental_id) FROM rental) + 1);

SELECT setval('staff_staff_id_seq', (SELECT MAX(staff_id) FROM staff) + 1);

SELECT setval('store_store_id_seq', (SELECT MAX(store_id) FROM store) + 1);
