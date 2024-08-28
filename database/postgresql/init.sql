-- CREATE TYPE user_role AS ENUM('Customer', 'Vendor', 'Admin');

CREATE TABLE users (
	user_id SERIAL PRIMARY KEY,
	email VARCHAR(50) NOT NULL,
	tckn CHAR(11) NOT NULL, -- opt FOR int DATATYPE?
	user_role user_role NOT NULL,
	created_at DATE DEFAULT CURRENT_DATE
);

CREATE TABLE login_info (
	user_id SERIAL REFERENCES users,
	username CHARACTER VARYING (32) UNIQUE NOT NULL,
	pwd_hash VARCHAR(60) NOT NULL -- CONVERT USING bcrypt
);

CREATE TABLE vendor(
	user_id SERIAL REFERENCES users,
	brand_name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE customer(
	user_id SERIAL REFERENCES users,
	full_name VARCHAR(64) NOT NULL
);

CREATE TABLE addresses (
	user_id SERIAL REFERENCES users,
	address_id SERIAL PRIMARY KEY,
	
	label_name VARCHAR(16) NOT NULL,
	receiver_fullname VARCHAR(64) NOT NULL,
	address TEXT NOT NULL,
	city CHAR(16) NOT NULL,
	country CHAR(16) NOT NULL,
	phone_nr CHAR(10) NOT NULL -- 0(XXX) XXX XX XX
);

-- mongodb'ye başlayınca geri dön:

--CREATE TABLE cart (
--	user_id REFERENCES users,
--	item_id text, -- DATA TYPE değişime açık
--	unit_price decimal(6, 2) -- TRY
--);
--
--CREATE TABLE purchases (
--
--);