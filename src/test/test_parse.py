from linkage.parse import to_patient
from copy import deepcopy

test_data = {
    "resourceType": "Patient",
    "gender": "male",
    "name": [
        {
            "family": "Washington",
            "given": ["Rob", "Robert"],
            "use": "legal",
            "suffix": "JR",
        }
    ],
    "address": [
        {
            "line": ["3461 Adams Neck", "Northeast"],
            "city": "Sarahbury",
            "state": "MA",
            "postalCode": "64832",
            "country": "USA",
        }
    ],
    "identifier": [
        {
            "system": "http://hospital.com/mrn",
            "value": "551-79-0423",
            "type": {
                "coding": [
                    {
                        "system": "http://hl7.org/fhir/v2/0203",
                        "code": "MR",
                        "display": "Medical Record Number",
                    }
                ],
                "text": "MRN",
            },
            "assigner": "Hospital",
        }
    ],
    "birthDate": "2003-10-08",
}


def test_all():
    patient_bundle = deepcopy(test_data)
    patient = to_patient(patient_bundle)
    assert (patient) is not None

    # Name
    assert (patient.name.family) == "Washington"
    assert (patient.name.given) == ["Rob", "Robert"]
    assert (patient.name.suffix) == "JR"
    assert (patient.name.use) == "legal"

    # Date of birth
    assert (patient.birthdate) == "2003-10-08"

    # Gender
    assert (patient.sex) == "male"

    # Address
    assert (len(patient.address.street)) == 2
    assert (patient.address.street) == ["3461 Adams Neck", "Northeast"]
    assert (patient.address.city) == "Sarahbury"
    assert (patient.address.state) == "MA"
    assert (patient.address.zip) == "64832"

    # Identification
    assert (len(patient.identifications)) == 1
    assert (patient.identifications[0].type) == "MR"
    assert (patient.identifications[0].value) == "551-79-0423"
    assert (patient.identifications[0].assigning_authority) == "Hospital"


def test_empty():
    patient = to_patient({})
    assert (patient) is None


# Name
def test_empty_name():
    patient_bundle = deepcopy(test_data)
    patient_bundle["name"] = []
    patient = to_patient(patient_bundle)
    assert (patient) is not None
    assert (patient.name) is None


def test_empty_name_family():
    patient_bundle = deepcopy(test_data)
    patient_bundle["name"][0]["family"] = None
    patient = to_patient(patient_bundle)
    assert (patient.name.family) is None
    assert (patient.name.given) == ["Rob", "Robert"]
    assert (patient.name.suffix) == "JR"
    assert (patient.name.use) == "legal"


def test_empty_name_given():
    patient_bundle = deepcopy(test_data)
    patient_bundle["name"][0]["given"] = None
    patient = to_patient(patient_bundle)
    assert (patient.name.given) is None
    assert (patient.name.family) == "Washington"
    assert (patient.name.suffix) == "JR"
    assert (patient.name.use) == "legal"


def test_empty_name_use():
    patient_bundle = deepcopy(test_data)
    patient_bundle["name"][0]["use"] = None
    patient = to_patient(patient_bundle)
    assert (patient.name.use) is None
    assert (patient.name.family) == "Washington"
    assert (patient.name.given) == ["Rob", "Robert"]
    assert (patient.name.suffix) == "JR"


def test_empty_name_suffix():
    patient_bundle = deepcopy(test_data)
    patient_bundle["name"][0]["suffix"] = None
    patient = to_patient(patient_bundle)
    assert (patient.name.suffix) is None
    assert (patient.name.family) == "Washington"
    assert (patient.name.given) == ["Rob", "Robert"]
    assert (patient.name.use) == "legal"


# Birth date
def test_empty_birth_date():
    patient_bundle = deepcopy(test_data)
    patient_bundle["birthDate"] = None
    patient = to_patient(patient_bundle)
    assert (patient.birthdate) is None


# Gender
def test_empty_gender():
    patient_bundle = deepcopy(test_data)
    patient_bundle["gender"] = None
    patient = to_patient(patient_bundle)
    assert (patient.sex) is None


# Address
def test_empty_address():
    patient_bundle = deepcopy(test_data)
    patient_bundle["address"] = None
    patient = to_patient(patient_bundle)
    assert patient.address is None


def test_empty_address_line():
    patient_bundle = deepcopy(test_data)
    patient_bundle["address"][0]["line"] = None
    patient = to_patient(patient_bundle)
    assert (patient.address.street) is None
    assert (patient.address.city) == "Sarahbury"
    assert (patient.address.state) == "MA"
    assert (patient.address.zip) == "64832"


def test_empty_address_city():
    patient_bundle = deepcopy(test_data)
    patient_bundle["address"][0]["city"] = None
    patient = to_patient(patient_bundle)
    assert (patient.address.street) == ["3461 Adams Neck", "Northeast"]
    assert (patient.address.city) is None
    assert (patient.address.state) == "MA"
    assert (patient.address.zip) == "64832"


def test_empty_address_state():
    patient_bundle = deepcopy(test_data)
    patient_bundle["address"][0]["state"] = None
    patient = to_patient(patient_bundle)
    assert (patient.address.street) == ["3461 Adams Neck", "Northeast"]
    assert (patient.address.city) == "Sarahbury"
    assert (patient.address.state) is None
    assert (patient.address.zip) == "64832"


def test_empty_address_zip():
    patient_bundle = deepcopy(test_data)
    patient_bundle["address"][0]["postalCode"] = None
    patient = to_patient(patient_bundle)
    assert (patient.address.street) == ["3461 Adams Neck", "Northeast"]
    assert (patient.address.city) == "Sarahbury"
    assert (patient.address.state) == "MA"
    assert (patient.address.zip) is None


# Identification
def test_empty_identification():
    patient_bundle = deepcopy(test_data)
    patient_bundle["identifier"] = None
    patient = to_patient(patient_bundle)
    assert (len(patient.identifications)) == 0


def test_empty_identification_type():
    patient_bundle = deepcopy(test_data)
    patient_bundle["identifier"][0]["type"] = None
    patient = to_patient(patient_bundle)
    assert (patient.identifications[0].type) is None


def test_empty_identification_type_coding():
    patient_bundle = deepcopy(test_data)
    patient_bundle["identifier"][0]["type"]["coding"] = None
    patient = to_patient(patient_bundle)
    assert (patient.identifications[0].type) is None


def test_empty_identification_type_code():
    patient_bundle = deepcopy(test_data)
    patient_bundle["identifier"][0]["type"]["coding"][0]["code"] = None
    patient = to_patient(patient_bundle)
    assert (patient.identifications[0].value) == "551-79-0423"
    assert (patient.identifications[0].assigning_authority) == "Hospital"
    assert (patient.identifications[0].type) is None


def test_empty_identification_type_value():
    patient_bundle = deepcopy(test_data)
    patient_bundle["identifier"][0]["value"] = None
    patient = to_patient(patient_bundle)
    assert (patient.identifications[0].value) is None
    assert (patient.identifications[0].assigning_authority) == "Hospital"
    assert (patient.identifications[0].type) == "MR"


def test_empty_identification_type_assigning_authority():
    patient_bundle = deepcopy(test_data)
    patient_bundle["identifier"][0]["assigner"] = None
    patient = to_patient(patient_bundle)
    assert (patient.identifications[0].value) == "551-79-0423"
    assert (patient.identifications[0].assigning_authority) is None
    assert (patient.identifications[0].type) == "MR"
