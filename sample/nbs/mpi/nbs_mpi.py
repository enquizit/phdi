import pyodbc
from pyodbc import Connection
from linkage.models.patient import Patient
from linkage.models.configuration import BlockCriteria, Field
from linkage.models.client import BaseMPIConnectorClient
from linkage.models.identification_types import IdentificationType
from linkage.block import get_block_value
from mpi.resolver.resolvers import (
    fetch_first_name_block,
    fetch_middle_name_block,
    fetch_second_middle_name_block,
    fetch_last_name_block,
    fetch_birthdate_block,
    fetch_address_block,
    fetch_city_block,
    fetch_state_block,
    fetch_zip_block,
    fetch_identification_block,
    fetch_patients,
)


class NbsMpiClient(BaseMPIConnectorClient):
    conn: Connection

    def __init__(self):
        DRIVER = "ODBC Driver"
        SERVER = "127.0.0.1"
        PORT = "1433"
        DATABASE = "NBS_ODSE"
        USERNAME = "sa"
        PASSWORD = "fake.fake.fake.1234"
        connection_string = f"DRIVER={DRIVER};SERVER={SERVER};PORT={PORT};DATABASE={DATABASE};UID={USERNAME};PWD={PASSWORD};TrustServerCertificate=yes;"
        self.conn = pyodbc.connect(connection_string)

    def get_patient_data(
        self, patient: Patient, criteria: list[BlockCriteria]
    ) -> list[Patient]:
        matching_patient_ids = set[int]()
        for block_criteria in criteria:
            field_value = get_block_value(patient, block_criteria)

            match block_criteria.field:
                case Field.FIRST_NAME:
                    matching_patient_ids.update(
                        fetch_first_name_block(
                            field_value, block_criteria.transform, self.conn
                        )
                    )
                case Field.LAST_NAME:
                    matching_patient_ids.update(
                        fetch_last_name_block(
                            field_value, block_criteria.transform, self.conn
                        )
                    )
                case Field.MIDDLE_NAME:
                    matching_patient_ids.update(
                        fetch_middle_name_block(
                            field_value, block_criteria.transform, self.conn
                        )
                    )
                case Field.SECOND_MIDDLE_NAME:
                    matching_patient_ids.update(
                        fetch_second_middle_name_block(
                            field_value, block_criteria.transform, self.conn
                        )
                    )
                case Field.BIRTHDATE:
                    # transform not currently supported
                    matching_patient_ids.update(
                        fetch_birthdate_block(field_value, self.conn)
                    )
                case Field.STREET_ADDRESS:
                    matching_patient_ids.update(
                        fetch_address_block(
                            field_value, block_criteria.transform, self.conn
                        )
                    )
                case Field.CITY:
                    matching_patient_ids.update(
                        fetch_city_block(
                            field_value, block_criteria.transform, self.conn
                        )
                    )
                case Field.STATE:
                    matching_patient_ids.update(
                        fetch_state_block(
                            field_value, block_criteria.transform, self.conn
                        )
                    )
                case Field.ZIP:
                    matching_patient_ids.update(
                        fetch_zip_block(
                            field_value, block_criteria.transform, self.conn
                        )
                    )
                case Field.IDENTIFICATION_MRN:
                    matching_patient_ids.update(
                        fetch_identification_block(
                            field_value,
                            IdentificationType.MEDICAL_RECORD_NUMBER,
                            block_criteria.transform,
                            self.conn,
                        )
                    )
                case Field.IDENTIFICATION_SSN:
                    matching_patient_ids.update(
                        fetch_identification_block(
                            field_value,
                            IdentificationType.SOCIAL_SECURITY_NUMBER,
                            block_criteria.transform,
                            self.conn,
                        )
                    )

        return fetch_patients(matching_patient_ids, self.conn)
