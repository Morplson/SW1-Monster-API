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

--INSERT INTO users (username, password, token, access_domain)
--VALUES ('admin', 'istrator', 'Basic admin-mtcgToken', '\S+');




-- create the cards table
DROP TABLE IF EXISTS cards CASCADE;
CREATE TABLE cards (
	id TEXT PRIMARY KEY,
	name TEXT NOT NULL,
	damage FLOAT NOT NULL DEFAULT 10,
	health FLOAT NOT NULL DEFAULT 20,
	critical_chance FLOAT NOT NULL DEFAULT 0.1,
	element_type TEXT NOT NULL DEFAULT 'NORMAL',
	monster_type TEXT NOT NULL DEFAULT 'SPELL'
);


-- create the stacks (users_cards) table
DROP TABLE IF EXISTS users_cards CASCADE;
CREATE TABLE users_cards (
  id SERIAL PRIMARY KEY,
  user_id TEXT REFERENCES users(username) ON DELETE CASCADE,
  card_id TEXT REFERENCES cards(id) ON DELETE CASCADE,
  deck BOOLEAN NOT NULL DEFAULT FALSE,
  last_updated TIMESTAMP NOT NULL DEFAULT NOW()
);

-- function
CREATE OR REPLACE FUNCTION check_deck_limit() RETURNS TRIGGER AS $$
BEGIN
  NEW.last_updated = NOW();
  IF ((TG_OP = 'INSERT' AND NEW.deck = TRUE) OR (NEW.deck = TRUE AND OLD.deck = FALSE)) THEN
    -- remove the first card from the deck if there are already 5 cards
    IF (SELECT COUNT(*) FROM users_cards WHERE user_id = NEW.user_id AND deck = TRUE) >= 4 THEN
      UPDATE users_cards SET deck = FALSE WHERE user_id = NEW.user_id AND id = (SELECT id FROM users_cards WHERE user_id = NEW.user_id AND deck = TRUE ORDER BY last_updated LIMIT 1);
    END IF;
  ELSEIF (TG_OP = 'INSERT' AND (SELECT COUNT(*) FROM users_cards WHERE user_id = NEW.user_id AND deck = TRUE) < 4) THEN
      NEW.deck = TRUE;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER check_deck_limit
BEFORE INSERT OR UPDATE ON users_cards
FOR EACH ROW
EXECUTE FUNCTION check_deck_limit();

--
CREATE OR REPLACE FUNCTION check_deck_limit_on_delete() RETURNS TRIGGER AS $$
BEGIN
  IF (OLD.deck = TRUE) THEN
    -- check if there are any other cards in the deck
    IF (SELECT COUNT(*) FROM users_cards WHERE user_id = OLD.user_id AND deck = TRUE) = 1 THEN
      -- if not, set a random card to deck=true
      UPDATE users_cards SET deck = TRUE WHERE user_id = OLD.user_id AND id = (SELECT id FROM users_cards WHERE user_id = OLD.user_id AND deck = FALSE ORDER BY RANDOM() LIMIT 1);
    END IF;
  END IF;
  RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER check_deck_limit_on_delete
AFTER DELETE ON users_cards
FOR EACH ROW
EXECUTE FUNCTION check_deck_limit_on_delete();


-- create the packs table
DROP TABLE IF EXISTS packs CASCADE;
CREATE TABLE packs(
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL DEFAULT 'Pack',
    price INT NOT NULL DEFAULT 5
);

-- create the packs_cards table
DROP TABLE IF EXISTS packs_cards CASCADE;
CREATE TABLE packs_cards (
	id SERIAL PRIMARY KEY,
	pack_id INT REFERENCES packs(id) ON DELETE CASCADE,
	card_id TEXT REFERENCES cards(id) ON DELETE CASCADE
);

-- create the battles table
DROP TABLE IF EXISTS battles CASCADE;
CREATE TABLE battles (
	id SERIAL PRIMARY KEY,
	user1 TEXT REFERENCES users(username) ON DELETE CASCADE,
	user2 TEXT REFERENCES users(username) ON DELETE CASCADE,
	log TEXT NOT NULL,
	result SMALLINT NOT NULL
);

-- create the deals table
DROP TABLE IF EXISTS deals CASCADE;
CREATE TABLE deals (
	id TEXT PRIMARY KEY,
	card_id TEXT REFERENCES cards(id) ON DELETE CASCADE,
	wanted_element TEXT, -- IF NULL THEN ANY element
	wanted_monster TEXT, -- IF NULL THEN ANY monster
	wanted_damage FLOAT -- IF NULL THEN ANY damage
);


select * from users;
select * from cards;
select * from packs;