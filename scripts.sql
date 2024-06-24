ALTER TABLE linkage_request
ALTER COLUMN id SET DEFAULT gen_random_uuid();


ALTER TABLE linkage_request_algorithm
ALTER COLUMN id SET DEFAULT gen_random_uuid();


ALTER TABLE patient
ALTER COLUMN id SET DEFAULT gen_random_uuid();


ALTER TABLE person
ALTER COLUMN id SET DEFAULT gen_random_uuid();


ALTER TABLE log_item
ALTER COLUMN id SET DEFAULT gen_random_uuid();

