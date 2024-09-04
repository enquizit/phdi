import pyodbc
from pyodbc import Connection
from linkage.models.patient import Patient
from linkage.models.configuration import BlockCriteria, Field
from linkage.models.client import BaseMPIConnectorClient
from linkage.models.identification_types import IdentificationType
from linkage.block import get_block_value
from nbs.mpi.resolver.resolvers import (
    resolve_first_name,
    resolve_middle_name,
    resolve_second_middle_name,
    resolve_last_name,
    resolve_birthdate,
    resolve_phone,
    resolve_address,
    resolve_city,
    resolve_state,
    resolve_zip,
    resolve_identification,
    resolve_patients,
    resolve_suffix,
    resolve_current_sex,
)


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
        matching_patient_ids = set[int]()
        for block_criteria in criteria:
            field_value = get_block_value(patient, block_criteria)

            match block_criteria.field:
                case Field.FIRST_NAME:
                    matching_patient_ids.update(
                        resolve_first_name(
                            field_value, block_criteria.transform, self.conn
                        )
                    )
                case Field.LAST_NAME:
                    matching_patient_ids.update(
                        resolve_last_name(
                            field_value, block_criteria.transform, self.conn
                        )
                    )
                case Field.MIDDLE_NAME:
                    matching_patient_ids.update(
                        resolve_middle_name(
                            field_value, block_criteria.transform, self.conn
                        )
                    )
                case Field.SECOND_MIDDLE_NAME:
                    matching_patient_ids.update(
                        resolve_second_middle_name(
                            field_value, block_criteria.transform, self.conn
                        )
                    )
                case Field.SUFFIX:
                    matching_patient_ids.update(
                        resolve_suffix(field_value, block_criteria.transform, self.conn)
                    )
                case Field.CURRENT_SEX:
                    # transform not currently supported
                    matching_patient_ids.update(
                        resolve_current_sex(field_value, self.conn)
                    )
                case Field.BIRTHDATE:
                    # transform not currently supported
                    matching_patient_ids.update(
                        resolve_birthdate(field_value, self.conn)
                    )
                case Field.TELEPHONE:
                    matching_patient_ids.update(
                        resolve_phone(field_value, block_criteria.transform, self.conn)
                    )
                case Field.STREET_ADDRESS:
                    matching_patient_ids.update(
                        resolve_address(
                            field_value, block_criteria.transform, self.conn
                        )
                    )
                case Field.CITY:
                    matching_patient_ids.update(
                        resolve_city(field_value, block_criteria.transform, self.conn)
                    )
                case Field.STATE:
                    matching_patient_ids.update(
                        resolve_state(field_value, block_criteria.transform, self.conn)
                    )
                case Field.ZIP:
                    matching_patient_ids.update(
                        resolve_zip(field_value, block_criteria.transform, self.conn)
                    )
                case Field.IDENTIFICATION_MRN:
                    matching_patient_ids.update(
                        resolve_identification(
                            field_value,
                            IdentificationType.MEDICAL_RECORD_NUMBER,
                            block_criteria.transform,
                            self.conn,
                        )
                    )
                case Field.IDENTIFICATION_SSN:
                    matching_patient_ids.update(
                        resolve_identification(
                            field_value,
                            IdentificationType.SOCIAL_SECURITY_NUMBER,
                            block_criteria.transform,
                            self.conn,
                        )
                    )

        return resolve_patients(matching_patient_ids, self.conn)
