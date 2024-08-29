-- Because 'IF NOT EXISTS' for types were too hard
DO $$ 
BEGIN
    CREATE TYPE user_role AS ENUM('Customer', 'Vendor', 'Admin');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

ALTER TYPE user_role ADD VALUE IF NOT EXISTS 'Customer';
ALTER TYPE user_role ADD VALUE IF NOT EXISTS 'Vendor';
ALTER TYPE user_role ADD VALUE IF NOT EXISTS 'Admin';
--

CREATE TABLE IF NOT EXISTS users (
	user_id SERIAL PRIMARY KEY,
	email VARCHAR(50) NOT NULL,
	tckn CHAR(11) NOT NULL, -- opt FOR int DATATYPE?
	user_role user_role NOT NULL,
	created_at DATE DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS login_info (
	user_id SERIAL REFERENCES users,
	username CHARACTER VARYING (32) UNIQUE NOT NULL,
	pwd_hash VARCHAR(60) NOT NULL -- CONVERT USING bcrypt
);

CREATE TABLE IF NOT EXISTS vendor(
	user_id SERIAL REFERENCES users,
	brand_name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS customer(
	user_id SERIAL REFERENCES users,
	full_name VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS addresses (
	user_id SERIAL REFERENCES users,
	address_id SERIAL PRIMARY KEY,
	
	label_name VARCHAR(16) NOT NULL,
	receiver_fullname VARCHAR(64) NOT NULL,
	address TEXT NOT NULL,
	city CHAR(16) NOT NULL,
	country CHAR(16) NOT NULL,
	phone_nr CHAR(10) NOT NULL -- 0(XXX) XXX XX XX
);