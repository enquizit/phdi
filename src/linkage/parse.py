from linkage.models.patient import (
    Patient,
    Name,
    Address,
    Identification,
    Telecom,
    System,
)
import fhirpathpy


FHIR_PATHS = {
    "name": fhirpathpy.compile("Patient.name[0]"),
    "birthdate": fhirpathpy.compile("Patient.birthDate"),
    "gender": fhirpathpy.compile("Patient.gender"),
    "address": fhirpathpy.compile("Patient.address[0]"),
    "identifications": fhirpathpy.compile("Patient.identifier"),
    "telecom": fhirpathpy.compile("Patient.telecom"),
}


# extracts relevant patient data from provided FHIR Patient bundle
def to_patient(patient_resource: dict) -> Patient:
    if patient_resource is None or len(patient_resource) == 0:
        return None
    return Patient(
        name=parse_name(patient_resource),
        birthdate=parse_birthdate(patient_resource),
        sex=parse_gender(patient_resource),
        address=parse_address(patient_resource),
        telecoms=parse_telecom(patient_resource),
        identifications=parse_identifications(patient_resource),
    )


def parse_name(patient_resource: str) -> Name:
    name: list[dict[str, str | list[str]]] = FHIR_PATHS["name"](patient_resource)
    if name is not None and len(name) > 0:
        return Name(
            name[0].get("use"),
            name[0].get("family"),
            name[0].get("suffix"),
            name[0].get("given"),
        )
    return None


def parse_address(patient_resource: str) -> Address | None:
    address: list[dict[str, str | list[str]]] = FHIR_PATHS["address"](patient_resource)
    if address is not None and len(address) > 0:
        return Address(
            address[0].get("city"),
            address[0].get("state"),
            address[0].get("postalCode"),
            address[0].get("line"),
        )
    return None


def parse_birthdate(patient_resource: str) -> str | None:
    birthdate: list[str] = FHIR_PATHS["birthdate"](patient_resource)
    return birthdate[0] if len(birthdate) > 0 else None


def parse_gender(patient_resource: str) -> str | None:
    gender = FHIR_PATHS["gender"](patient_resource)
    return gender[0] if len(gender) > 0 else None


def parse_identifications(patient_resource: str) -> list[Identification]:
    identifications: list[dict[str, str | list[str]]] = FHIR_PATHS["identifications"](
        patient_resource
    )
    return [
        Identification(
            parse_identification_type(entry),
            entry.get("value"),
            entry.get("assigner"),
        )
        for entry in identifications
    ]


def parse_identification_type(entry: dict) -> str | None:
    type_dict = entry.get("type")
    if type_dict is None:
        return None
    coding = type_dict.get("coding")
    return coding[0].get("code") if coding is not None and len(coding) > 0 else None


def parse_telecom(patient_resource: str) -> list[Telecom]:
    telecoms: list[dict[str, str | list[str]]] = FHIR_PATHS["telecom"](patient_resource)
    return [
        Telecom(
            entry.get("value"),
            System.from_str(entry.get("system")),
        )
        for entry in telecoms
    ]
