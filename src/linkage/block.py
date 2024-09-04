from linkage.models.identification_types import IdentificationType
from linkage.models.patient import Patient, Identification
from linkage.models.configuration import BlockCriteria, Transform, Field


# get transformed value from provided patient based on the blocking criteria provided
def get_block_value(patient: Patient, block_criteria: BlockCriteria) -> str | None:
    field_value = get_field_value(patient, block_criteria.field)
    return transform_value(field_value, block_criteria.transform)


def get_field_value(patient: Patient, field: Field) -> str | None:
    """
    Get the value for the specified field from the patient
    """
    match field:
        case Field.FIRST_NAME:
            return patient.get_first_name()
        case Field.LAST_NAME:
            return patient.get_last_name()
        case Field.MIDDLE_NAME:
            return patient.get_middle_name()
        case Field.SECOND_MIDDLE_NAME:
            return patient.get_second_middle_name()
        case Field.BIRTHDATE:
            return patient.birthdate
        case Field.STREET_ADDRESS:
            return patient.get_street_address()
        case Field.CITY:
            return patient.get_city()
        case Field.STATE:
            return patient.get_state()
        case Field.ZIP:
            return patient.get_zip()
        case Field.IDENTIFICATION_MRN:
            id_type = IdentificationType.MEDICAL_RECORD_NUMBER
            return patient.get_id(id_type)
        case Field.IDENTIFICATION_SSN:
            id_type = IdentificationType.SOCIAL_SECURITY_NUMBER
            return patient.get_id(id_type)


def transform_value(value: str | None, transform: Transform | None) -> str | None:
    if value is None or value.strip() == "":
        return None
    if transform is None:
        return value
    match transform:
        case Transform.FIRST_FOUR:
            return value[: min(4, len(value))]
        case Transform.LAST_FOUR:
            return value[-4:]
        case _:
            return value


def transform_identification(
    identification: Identification | None, transform: Transform | None
) -> str | None:
    if identification is None or identification.value is None:
        return None
    return transform_value(identification.value, transform)
