CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;

CREATE TABLE object_location
(
    id          BIGSERIAL      NOT NULL,
    object_id   VARCHAR(36)    NOT NULL,
    received_at TIMESTAMP      NOT NULL,
    latitude    DECIMAL(11, 8) NOT NULL,
    longitude   DECIMAL(11, 8) NOT NULL
);

CREATE INDEX location_id_idx ON object_location (id DESC);
CREATE INDEX location_object_id_idx ON object_location (object_id);

SELECT create_hypertable('object_location', 'received_at');

