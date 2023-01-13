\c postgres

-- create the database

DROP DATABASE IF EXISTS monster_trading_cards;
CREATE DATABASE monster_trading_cards;

-- connect to the database
\c monster_trading_cards;

-- create the users table
DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
	username TEXT NOT NULL PRIMARY KEY,
	password TEXT NOT NULL,
	token TEXT NOT NULL UNIQUE,
	access_domain TEXT NOT NULL DEFAULT '',
	coins INT NOT NULL DEFAULT 20,
	name TEXT NOT NULL DEFAULT '',
	bio TEXT NOT NULL DEFAULT '',
	image TEXT NOT NULL DEFAULT '',
	elo_value INT NOT NULL DEFAULT 1000
);

INSERT INTO users (username, password, token, access_domain)
VALUES ('admin', 'istrator', 'Basic admin-mtcgToken', '\S+');




-- create the cards table
DROP TABLE IF EXISTS cards;
CREATE TABLE cards (
	id TEXT PRIMARY KEY,
	name TEXT NOT NULL,
	damage INT NOT NULL DEFAULT 10,
	health INT NOT NULL DEFAULT 20,
	critical_chance FLOAT NOT NULL DEFAULT 0.1,
	element_type TEXT NOT NULL DEFAULT 'NORMAL',
	monster_type TEXT NOT NULL DEFAULT 'SPELL'
);

-- create the stacks (users_cards) table
DROP TABLE IF EXISTS stacks;
CREATE TABLE stacks (
	id SERIAL PRIMARY KEY,
	user_id TEXT REFERENCES users(username) ON DELETE CASCADE,
	card_id TEXT REFERENCES cards(id) ON DELETE CASCADE
);

-- create the decks table
DROP TABLE IF EXISTS decks;

CREATE TABLE decks (
	id SERIAL PRIMARY KEY,
	user_id TEXT REFERENCES users(username) ON DELETE CASCADE,
	card_id TEXT REFERENCES cards(id) ON DELETE CASCADE
);

-- create the packs table
DROP TABLE IF EXISTS packs;
CREATE TABLE packs (
	id SERIAL PRIMARY KEY,
	name TEXT NOT NULL,
	price INT NOT NULL DEFAULT 5
);

-- create the packs_cards table
DROP TABLE IF EXISTS packs_cards;
CREATE TABLE packs_cards (
	id SERIAL PRIMARY KEY,
	deck_id INT REFERENCES packs(id) ON DELETE CASCADE,
	card_id TEXT REFERENCES cards(id) ON DELETE CASCADE
);

-- create the battles table
DROP TABLE IF EXISTS battles;
CREATE TABLE battles (
	id SERIAL PRIMARY KEY,
	user1 TEXT REFERENCES users(username) ON DELETE CASCADE,
	user2 TEXT REFERENCES users(username) ON DELETE CASCADE,
	log TEXT NOT NULL,
	result SMALLINT NOT NULL
);

-- create the deals table
DROP TABLE IF EXISTS deals;
CREATE TABLE deals (
	id TEXT PRIMARY KEY,
	card_id TEXT REFERENCES cards(id) ON DELETE CASCADE,
	wanted_element TEXT, -- IF NULL THEN ANY element
	wanted_monster TEXT, -- IF NULL THEN ANY monster
	wanted_damage FLOAT -- IF NULL THEN ANY damage
);


select * from users;