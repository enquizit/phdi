
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";



-- public.algorithm definition
;

CREATE TABLE  IF NOT EXISTS public.algorithm (
	id bigserial NOT NULL,
	"name" varchar(255) NOT NULL,
	"type" varchar(255) NOT NULL,
	CONSTRAINT algorithm_pkey PRIMARY KEY (id),
	CONSTRAINT uk_v5enjig6p4w8ad8vlxy3hysg UNIQUE (name)
);


-- public.algorithm_parameter definition



CREATE TABLE  IF NOT EXISTS public.algorithm_parameter (
	id bigserial NOT NULL,
	"name" varchar(255) NOT NULL,
	value varchar(255) NOT NULL,
	CONSTRAINT algorithm_parameter_pkey PRIMARY KEY (id)
);


-- public.field definition



CREATE TABLE  IF NOT EXISTS public.field (
	id bigserial NOT NULL,
	"name" varchar(255) NOT NULL,
	CONSTRAINT field_pkey PRIMARY KEY (id)
);


-- public.linkage_request definition


CREATE TABLE  IF NOT EXISTS public.linkage_request (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	CONSTRAINT linkage_request_pkey PRIMARY KEY (id)
);




-- public.person definition


CREATE TABLE  IF NOT EXISTS public.person (
	id uuid DEFAULT uuid_generate_v4() NOT NULL,
	CONSTRAINT person_pkey PRIMARY KEY (id)
);


-- public.transformation_type definition


CREATE TABLE  IF NOT EXISTS public.transformation_type (
	id bigserial NOT NULL,
	"name" varchar(255) NOT NULL,
	CONSTRAINT transformation_type_pkey PRIMARY KEY (id)
);


-- public.algorithm_parameter_mapping definition



CREATE TABLE  IF NOT EXISTS public.algorithm_parameter_mapping (
	id bigserial NOT NULL,
	parameter_value varchar(255) NOT NULL,
	algorithm_id int8 NOT NULL,
	parameter_id int8 NOT NULL,
	CONSTRAINT algorithm_parameter_mapping_pkey PRIMARY KEY (id),
	CONSTRAINT fk2jye0q5nrs4ettbrv955mavin FOREIGN KEY (parameter_id) REFERENCES public.algorithm_parameter(id),
	CONSTRAINT fkte87wdxxyn3fxcgti5jl1bfre FOREIGN KEY (algorithm_id) REFERENCES public.algorithm(id)
);


-- public.blocking_field definition


CREATE TABLE  IF NOT EXISTS public.blocking_field (
	id bigserial NOT NULL,
	algorithm_id int8 NOT NULL,
	field_id int8 NOT NULL,
	transformation_type int8 NULL,
	CONSTRAINT blocking_field_pkey PRIMARY KEY (id),
	CONSTRAINT fk6gv43m0tuar4gg129eheiv2p4 FOREIGN KEY (transformation_type) REFERENCES public.transformation_type(id),
	CONSTRAINT fkssnbtgwmuch4eocqvewperytd FOREIGN KEY (algorithm_id) REFERENCES public.algorithm(id),
	CONSTRAINT fktnuxdrsvekbgo49po8nnm5ra7 FOREIGN KEY (field_id) REFERENCES public.field(id)
);


-- public.criteria definition


CREATE TABLE  IF NOT EXISTS public.criteria (
	id bigserial NOT NULL,
	function_name varchar(255) NOT NULL,
	algorithm_id int8 NOT NULL,
	field_id int8 NOT NULL,
	threshold double precision NOT NULL,
	CONSTRAINT criteria_pkey PRIMARY KEY (id),
	CONSTRAINT fk73u1s8etqhathsukl9gtclv8d FOREIGN KEY (field_id) REFERENCES public.field(id),
	CONSTRAINT fkcs1iei7sb7v6yp4y5yh39ly4c FOREIGN KEY (algorithm_id) REFERENCES public.algorithm(id)
);


-- public.external_person definition



CREATE TABLE  IF NOT EXISTS public.external_person (
	id bigserial NOT NULL,
	person_id uuid NULL,
	CONSTRAINT external_person_pkey PRIMARY KEY (id),
	CONSTRAINT uk_aubrhj16c1c28gwfp5kb7twdi UNIQUE (person_id),
	CONSTRAINT fkym23gyk9ta8kdh492d6d9org FOREIGN KEY (person_id) REFERENCES public.person(id)
);


-- public.linkage_request_algorithm definition



CREATE TABLE  IF NOT EXISTS public.linkage_request_algorithm (
	id uuid DEFAULT gen_random_uuid() NOT NULL,
	algorithm_id int8 NOT NULL,
	linkage_request_id uuid NOT NULL,
	CONSTRAINT linkage_request_algorithm_pkey PRIMARY KEY (id),
	CONSTRAINT fk86fibipt6lb7u59yn4b70m0r0 FOREIGN KEY (algorithm_id) REFERENCES public.algorithm(id),
	CONSTRAINT fkbj5e37x09wdeu4wrllullfj5c FOREIGN KEY (linkage_request_id) REFERENCES public.linkage_request(id)
);


-- public.log_item definition



CREATE TABLE  IF NOT EXISTS public.log_item (
	id bigserial NOT NULL,
	created_timestamp timestamp(6)  NULL,
	log_level varchar(255) NULL,
	logger varchar(255) NULL,
	message varchar(512) NULL,
	trace varchar(255) NULL,
	linkage_request_id uuid NOT NULL,
	CONSTRAINT log_item_pkey PRIMARY KEY (id),
	CONSTRAINT fkdp2yfyqdugkhjqask1deqedqp FOREIGN KEY (linkage_request_id) REFERENCES public.linkage_request(id)
);






-- public.patient definition



CREATE TABLE  IF NOT EXISTS public.patient (
	id uuid DEFAULT uuid_generate_v4() NOT NULL,
	"key" varchar(255) NOT NULL,
	value varchar(255) NULL,
	person_id uuid NOT NULL,
	patient_id uuid NOT NULL ,
	created_at timestamp(6) NOT NULL,
	CONSTRAINT patient_pkey PRIMARY KEY (id),
	CONSTRAINT patient_person_id_fkey FOREIGN KEY (person_id) REFERENCES public.person(id)
);


-- public.field_score definition
CREATE TABLE field_score (
    id bigserial PRIMARY KEY,
    request_id UUID,
    person_id UUID,
    patient_id UUID,
    field_name VARCHAR(255),
    score DOUBLE PRECISION,
    CONSTRAINT field_score_person_id_fkey FOREIGN KEY (person_id) REFERENCES public.person(id)
);



