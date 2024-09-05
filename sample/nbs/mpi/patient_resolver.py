from pyodbc import Connection, Row
from nbs.mpi.data_converter import (
    to_name,
    to_address,
    to_identifications,
    to_telecom_phone,
    to_telecom_email,
)
from linkage.models.patient import Patient

query = """
SELECT 
  p.person_uid,
  p.person_parent_uid,
  p.birth_time,
  p.curr_sex_cd, 
  nested.address, 
  nested.phone, 
  nested.email, 
  nested.name, 
  nested.identification 
FROM 
  person p WITH (NOLOCK)
  OUTER apply (
    SELECT 
      * 
    FROM 
      -- address
      (
        SELECT 
          (
            SELECT 
              STRING_ESCAPE(pl.street_addr1, 'json') street1, 
              STRING_ESCAPE(pl.street_addr2, 'json') street2, 
              STRING_ESCAPE(pl.city_desc_txt, 'json') city, 
              [state].state_nm state,
              pl.zip_cd zip
            FROM 
              Entity_locator_participation elp WITH (NOLOCK)
              JOIN Postal_locator pl WITH (NOLOCK) ON elp.locator_uid = pl.postal_locator_uid 
			  left join NBS_SRTE..State_code [state] on [state].state_cd = pl.state_cd
            WHERE 
              elp.entity_uid = p.person_uid 
              AND elp.class_cd = 'PST' 
              AND elp.status_cd = 'A'
              AND elp.record_status_cd = 'ACTIVE'
          	ORDER BY elp.as_of_date DESC
            FOR json path, 
              INCLUDE_NULL_VALUES
          ) AS address
      ) AS address, 
      -- person phone
      (
        SELECT 
          (
            SELECT
			  REPLACE(REPLACE(tl.phone_nbr_txt,'-',''),' ','') value
            FROM 
              Entity_locator_participation elp WITH (NOLOCK)
              JOIN Tele_locator tl WITH (NOLOCK) ON elp.locator_uid = tl.tele_locator_uid 
            WHERE 
              elp.entity_uid = p.person_uid 
              AND elp.class_cd = 'TELE' 
              AND elp.status_cd = 'A' 
              AND elp.record_status_cd = 'ACTIVE'
              AND tl.phone_nbr_txt IS NOT NULL
              ORDER BY elp.as_of_date DESC
            FOR json path, 
              INCLUDE_NULL_VALUES
          ) AS phone
      ) AS phone, 
      -- person email
      (
        SELECT 
          (
            SELECT 
              STRING_ESCAPE(tl.email_address, 'json') value 
            FROM 
              Entity_locator_participation elp WITH (NOLOCK)
              JOIN Tele_locator tl WITH (NOLOCK) ON elp.locator_uid = tl.tele_locator_uid 
            WHERE 
              elp.entity_uid = p.person_uid 
              AND elp.class_cd = 'TELE' 
              AND elp.status_cd = 'A' 
              AND elp.record_status_cd = 'ACTIVE'
              AND tl.email_address IS NOT NULL
              ORDER BY elp.as_of_date DESC
            FOR json path, 
              INCLUDE_NULL_VALUES
          ) AS email
      ) AS email, 
      -- person_names
      (
        SELECT 
          (
            SELECT 
			  STRING_ESCAPE(pn.last_nm, 'json') lastNm,
			  STRING_ESCAPE(pn.last_nm2, 'json') lastNm2,
              STRING_ESCAPE(pn.middle_nm, 'json') middleNm,
              STRING_ESCAPE(pn.middle_nm2, 'json') middleNm2,
              STRING_ESCAPE(pn.first_nm, 'json') firstNm,
              pn.nm_suffix nmSuffix, 
              pn.nm_use_cd nmUse 
            FROM 
              person_name pn WITH (NOLOCK)
            WHERE 
              person_uid = p.person_uid 
              AND pn.status_cd = 'A'
              AND pn.record_status_cd = 'ACTIVE'
            ORDER BY pn.as_of_date DESC
            FOR json path, 
              INCLUDE_NULL_VALUES
          ) AS name
      ) AS name, 
      -- Entity id
      (
        SELECT 
          (
            SELECT 
              ei.type_cd [type], 
              ei.assigning_authority_cd assigningAuthority, 
			  STRING_ESCAPE(REPLACE(REPLACE(ei.root_extension_txt,'-',''),' ',''), 'json') [value]
            FROM 
              entity_id ei WITH (NOLOCK)
            WHERE 
              ei.entity_uid = p.person_uid
              AND ei.record_status_cd = 'ACTIVE'
              AND ei.status_cd = 'A'
            ORDER BY ei.as_of_date DESC
          	FOR json path, 
              INCLUDE_NULL_VALUES
          ) AS identification
      ) AS identification
  ) AS nested 
WHERE 
p.person_parent_uid in (
"""


def fetch_patients(ids: set[int], connection: Connection) -> list[Patient]:
    if len(ids) == 0:
        return []
    placeholders = ",".join(["?"] * len(ids))
    patient_query = f"{query}{placeholders});"

    with connection.cursor() as cursor:
        cursor.execute(patient_query, list(ids))
        records = cursor.fetchall()
        return [to_patient(x) for x in records]


def to_patient(sql_result: Row) -> Patient:
    birth = sql_result.birth_time
    birthdate = f"{birth.year}-{birth.month}-{birth.day}" if birth is not None else None
    gender = sql_result.curr_sex_cd
    patient_id = sql_result.person_uid
    person_id = sql_result.person_parent_uid
    name = to_name(sql_result.name)
    address = to_address(sql_result.address)
    telecoms = to_telecom_phone(sql_result.phone) + to_telecom_email(sql_result.email)
    identifications = to_identifications(sql_result.identification)
    patient = Patient(
        birthdate=birthdate,
        sex=gender,
        name=name,
        address=address,
        telecoms=telecoms,
        identifications=identifications,
        patient_id=patient_id,
        person_id=person_id,
    )
    return patient
