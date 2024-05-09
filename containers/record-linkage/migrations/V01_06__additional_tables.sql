BEGIN;

CREATE TABLE IF NOT EXISTS request (
    request_id UUID DEFAULT uuid_generate_v4 (),
    PRIMARY KEY (request_id)
);

COMMIT;
