CREATE TYPE ${flyway:defaultSchema}.type_id            		 FROM VARCHAR(100);
GO		 
		 
CREATE TYPE ${flyway:defaultSchema}.type_code          		 FROM VARCHAR(50);
GO		 
		 
CREATE TYPE ${flyway:defaultSchema}.type_shortdescr    		 FROM VARCHAR(100);
GO

CREATE TYPE ${flyway:defaultSchema}.type_longdescr     		 FROM VARCHAR(MAX);
GO

CREATE TYPE ${flyway:defaultSchema}.type_timestamp     		 FROM DATETIME2;
GO

CREATE TYPE ${flyway:defaultSchema}.type_date          		 FROM DATE;
GO

CREATE TYPE ${flyway:defaultSchema}.type_geocoordinate 		 FROM DECIMAL(9,6);
GO

CREATE TYPE ${flyway:defaultSchema}.type_uuid 		   		 FROM UNIQUEIDENTIFIER;
GO

CREATE TYPE ${flyway:defaultSchema}.type_shorterdescr  		 FROM VARCHAR(7);
GO

CREATE TYPE ${flyway:defaultSchema}.type_shorterdescr_255    FROM VARCHAR(255);
GO

CREATE TYPE ${flyway:defaultSchema}.type_int    			 FROM INT;
GO

CREATE TYPE ${flyway:defaultSchema}.type_shorterdescr_20     FROM VARCHAR(20);
GO

CREATE TYPE ${flyway:defaultSchema}.type_shorterdescr_10     FROM VARCHAR(10);
GO

CREATE TABLE ${flyway:defaultSchema}.person (
    person_id ${flyway:defaultSchema}.type_uuid NOT NULL DEFAULT NEWID(),
    CONSTRAINT PK_${flyway:defaultSchema}_person PRIMARY KEY CLUSTERED (person_id)
);
GO

CREATE TABLE ${flyway:defaultSchema}.patient (
    patient_id ${flyway:defaultSchema}.type_uuid DEFAULT NEWID(),
    person_id ${flyway:defaultSchema}.type_uuid,
    dob ${flyway:defaultSchema}.type_date,
    sex ${flyway:defaultSchema}.type_shorterdescr ,
    race ${flyway:defaultSchema}.type_shortdescr,
    ethnicity ${flyway:defaultSchema}.type_shortdescr,
    CONSTRAINT PK_${flyway:defaultSchema}_Patient PRIMARY KEY CLUSTERED (patient_id),
	CONSTRAINT fk_patient_to_person FOREIGN KEY (person_id)
        REFERENCES ${flyway:defaultSchema}.person (person_id)
);
GO

CREATE TABLE ${flyway:defaultSchema}.name (
    name_id ${flyway:defaultSchema}.type_uuid DEFAULT NEWID(),
    patient_id ${flyway:defaultSchema}.type_uuid,
    last_name ${flyway:defaultSchema}.type_shorterdescr_255,
    type ${flyway:defaultSchema}.type_shortdescr,
    CONSTRAINT PK_${flyway:defaultSchema}_Name PRIMARY KEY CLUSTERED (name_id),
    CONSTRAINT fk_name_to_patient FOREIGN KEY (patient_id)
        REFERENCES ${flyway:defaultSchema}.patient (patient_id)
);
GO

CREATE TABLE ${flyway:defaultSchema}.given_name (
    given_name_id ${flyway:defaultSchema}.type_uuid DEFAULT NEWID(),
    name_id ${flyway:defaultSchema}.type_uuid,
    given_name ${flyway:defaultSchema}.type_shorterdescr_255,
    given_name_index ${flyway:defaultSchema}.type_int,
    CONSTRAINT PK_${flyway:defaultSchema}_Given_name PRIMARY KEY CLUSTERED (given_name_id),
	CONSTRAINT fk_given_to_name FOREIGN KEY (name_id)
        REFERENCES ${flyway:defaultSchema}.name (name_id)
);
GO

ALTER TABLE ${flyway:defaultSchema}.given_name NOCHECK CONSTRAINT fk_given_to_name;
GO


CREATE TABLE ${flyway:defaultSchema}.identifier (
    identifier_id ${flyway:defaultSchema}.type_uuid DEFAULT NEWID(),
    patient_id ${flyway:defaultSchema}.type_uuid,
    patient_identifier ${flyway:defaultSchema}.type_shorterdescr_255,
    type_code ${flyway:defaultSchema}.type_shorterdescr_255,
    type_display ${flyway:defaultSchema}.type_shorterdescr_255,
    type_system ${flyway:defaultSchema}.type_shorterdescr_255,
    CONSTRAINT PK_${flyway:defaultSchema}_Identifier PRIMARY KEY CLUSTERED (identifier_id),
	CONSTRAINT fk_ident_to_patient FOREIGN KEY (patient_id)
        REFERENCES ${flyway:defaultSchema}.patient (patient_id)
);
GO

CREATE TABLE ${flyway:defaultSchema}.phone_number (
    phone_id ${flyway:defaultSchema}.type_uuid DEFAULT NEWID(),
    patient_id ${flyway:defaultSchema}.type_uuid,
    phone_number ${flyway:defaultSchema}.type_shorterdescr_20,
    type ${flyway:defaultSchema}.type_shortdescr,
    start_date ${flyway:defaultSchema}.type_timestamp,
    end_date ${flyway:defaultSchema}.type_timestamp,
    CONSTRAINT PK_${flyway:defaultSchema}_Phone_number PRIMARY KEY CLUSTERED (phone_id),
	CONSTRAINT fk_phone_to_patient FOREIGN KEY (patient_id)
        REFERENCES ${flyway:defaultSchema}.patient (patient_id)
);
GO

CREATE TABLE ${flyway:defaultSchema}.address (
    address_id ${flyway:defaultSchema}.type_uuid DEFAULT NEWID(),
    patient_id ${flyway:defaultSchema}.type_uuid,
    type ${flyway:defaultSchema}.type_shortdescr,
    line_1 ${flyway:defaultSchema}.type_shortdescr,
    line_2 ${flyway:defaultSchema}.type_shortdescr,
    city ${flyway:defaultSchema}.type_shorterdescr_255,
    zip_code ${flyway:defaultSchema}.type_shorterdescr_10,
    state ${flyway:defaultSchema}.type_shortdescr,
    country ${flyway:defaultSchema}.type_shorterdescr_255,
    latitude ${flyway:defaultSchema}.type_geocoordinate,
    longitude ${flyway:defaultSchema}.type_geocoordinate,
    start_date ${flyway:defaultSchema}.type_timestamp,
    end_date ${flyway:defaultSchema}.type_timestamp,
    CONSTRAINT PK_${flyway:defaultSchema}_Address PRIMARY KEY CLUSTERED (address_id),
	CONSTRAINT fk_addr_to_patient FOREIGN KEY (patient_id)
        REFERENCES ${flyway:defaultSchema}.patient (patient_id)
);
GO

CREATE TABLE ${flyway:defaultSchema}.external_source (
    external_source_id ${flyway:defaultSchema}.type_uuid DEFAULT NEWID(),
    external_source_name ${flyway:defaultSchema}.type_shorterdescr_255,
    external_source_description ${flyway:defaultSchema}.type_shorterdescr_255,
    CONSTRAINT PK_${flyway:defaultSchema}_External_source PRIMARY KEY CLUSTERED (external_source_id)
);
GO

CREATE TABLE ${flyway:defaultSchema}.external_person (
    external_id ${flyway:defaultSchema}.type_uuid DEFAULT NEWID(),
    person_id ${flyway:defaultSchema}.type_uuid,
    external_person_id ${flyway:defaultSchema}.type_shorterdescr_255,
    external_source_id ${flyway:defaultSchema}.type_uuid,
    CONSTRAINT PK_${flyway:defaultSchema}_External_person PRIMARY KEY CLUSTERED (external_id),
	CONSTRAINT fk_ext_person_to_person FOREIGN KEY (person_id)
        REFERENCES ${flyway:defaultSchema}.person (person_id),
	CONSTRAINT fk_ext_person_to_source FOREIGN KEY (external_source_id)
        REFERENCES ${flyway:defaultSchema}.external_source (external_source_id)
);
GO

INSERT INTO ${flyway:defaultSchema}.external_source (${flyway:defaultSchema}.external_source_id, ${flyway:defaultSchema}.external_source_name, ${flyway:defaultSchema}.external_source_description)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380b79','IRIS','LACDPH Surveillance System');
GO

/* 

FOREIGN KEY INDEXES

Postgres does not automatically create indexes for foreign keys. 
Testing has shown that indexing foreign keys improves performance by 10x or more.

*/
CREATE INDEX  address_patient_id_index ON ${flyway:defaultSchema}.address (patient_id);
GO

CREATE INDEX  identifier_patient_id_index ON ${flyway:defaultSchema}.identifier (patient_id);
GO

CREATE INDEX  name_patient_id_index ON ${flyway:defaultSchema}.name (patient_id);
GO

CREATE INDEX  given_name_name_id_index ON ${flyway:defaultSchema}.given_name (name_id);
GO

CREATE INDEX  phone_number_patient_id_index ON ${flyway:defaultSchema}.phone_number (patient_id);
GO

CREATE INDEX  patient_person_id_index ON ${flyway:defaultSchema}.patient (person_id);
GO

CREATE INDEX  external_person_person_id_index ON ${flyway:defaultSchema}.external_person (person_id);
GO

CREATE INDEX  external_person_external_source_id_index ON ${flyway:defaultSchema}.external_person (external_source_id);
GO

/* 

Additional Indexes for identifier type and given name index.

*/

CREATE INDEX identifier_type_index ON ${flyway:defaultSchema}.identifier (type_code);
GO

CREATE INDEX given_name_index_index ON ${flyway:defaultSchema}.given_name (given_name_index);
GO




