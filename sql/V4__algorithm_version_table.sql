

CREATE TABLE  IF NOT EXISTS public.algorithm_version (
    id bigserial NOT NULL,
    algorithm_id int8 NOT NULL,
    version_id BIGINT,
    algorithm_json TEXT,
    created_at TIMESTAMP,
    CONSTRAINT algorithm_version_pkey PRIMARY KEY (id),
    CONSTRAINT algorithm_version_algorithm FOREIGN KEY (algorithm_id) REFERENCES public.algorithm(id)
);
