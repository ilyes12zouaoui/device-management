CREATE SEQUENCE IF NOT EXISTS device_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE devices
(
    id              BIGSERIAL,
    name            VARCHAR(255)             NOT NULL,
    brand           VARCHAR(255)             NOT NULL,
    device_state    VARCHAR(255)             NOT NULL,
    creation_time   TIMESTAMP WITH TIME ZONE NOT NULL,
    created_on      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_updated_on TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_device PRIMARY KEY (id)
);