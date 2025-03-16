CREATE TYPE booking_status AS ENUM ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT unique_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(1024) NOT NULL,
    requester_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(512) NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    request_id BIGINT REFERENCES requests (id) ON DELETE CASCADE,
    CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE  IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL REFERENCES items (id) ON DELETE CASCADE,
    booker_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status booking_status NOT NULL,
    CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR (1024) NOT NULL,
    item_id BIGINT NOT NULL REFERENCES items (id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id)
);