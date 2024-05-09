BEGIN;

CREATE TABLE IF NOT EXISTS log_item (
    id UUID DEFAULT uuid_generate_v4 (),
    logger VARCHAR(100),
    log_level  VARCHAR(20),
    trace TEXT,
    msg TEXT,
	created_at TIMESTAMP,
    PRIMARY KEY (id)
);

COMMIT;
