import pyodbc
from pyodbc import Connection
from linkage.models.patient import Patient
from linkage.models.configuration import BlockCriteria
from linkage.models.client import BaseMPIConnectorClient
from linkage.block import get_block_value
from nbs.mpi.criteria_mapper import map_to_query
from nbs.mpi.patient_resolver import fetch_patients

base_query = """
    SELECT DISTINCT
        p.person_parent_uid
    FROM
        person p
        LEFT JOIN person_name pn ON p.person_uid = pn.person_uid
            AND pn.record_status_cd = 'ACTIVE'
            AND pn.nm_use_cd = 'L'
        LEFT JOIN Entity_locator_participation elp ON elp.entity_uid = p.person_uid
            AND elp.record_status_cd = 'ACTIVE'
        LEFT JOIN postal_locator pl ON elp.locator_uid = pl.postal_locator_uid
            AND elp.class_cd = 'PST'
        LEFT JOIN tele_locator tl ON elp.locator_uid = tl.tele_locator_uid
            AND elp.class_cd = 'TELE'
        LEFT JOIN entity_id id ON p.person_uid = id.entity_uid
            AND id.record_status_cd = 'ACTIVE'
    WHERE
        p.cd = 'PAT'
        AND p.record_status_cd = 'ACTIVE'
"""


class NbsMpiClient(BaseMPIConnectorClient):
    conn: Connection

    def __init__(self):
        # View installed drivers: odbcinst -j
        DRIVER = "ODBC Driver 18 for SQL Server"
        SERVER = "nbs-mssql"
        PORT = "1433"
        DATABASE = "NBS_ODSE"
        USERNAME = "sa"
        PASSWORD = "fake.fake.fake.1234"
        connection_string = f"DRIVER={DRIVER};SERVER={SERVER};PORT={PORT};DATABASE={DATABASE};UID={USERNAME};PWD={PASSWORD};TrustServerCertificate=yes;"
        self.conn = pyodbc.connect(connection_string)

    def get_patient_data(
        self, patient: Patient, criteria: list[BlockCriteria]
    ) -> list[Patient]:
        query = str(base_query)
        parameters: list[str | None] = []
        for block_criteria in criteria:
            field_value = get_block_value(patient, block_criteria)

            # Add the where clause to the query
            search_query = map_to_query(field_value, block_criteria)
            query += search_query.query
            parameters += search_query.params

        with self.conn.cursor() as cursor:
            cursor.execute(query, parameters)
            records = cursor.fetchall()
            patient_ids: set[int] = set([r.person_parent_uid for r in records])
            return fetch_patients(patient_ids, self.conn)
