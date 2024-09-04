from pyodbc import Connection
from linkage.models.configuration import Transform
from linkage.models.patient import Patient
from linkage.models.identification_types import IdentificationType
from mpi.resolver.name_resolver import (
    fetch_first_name_block,
    fetch_last_name_block,
    fetch_middle_name_block,
    fetch_suffix_block,
    fetch_second_middle_name_block,
)
from mpi.resolver.birthdate_resolver import fetch_birthdate_block
from mpi.resolver.address_resolver import (
    fetch_address_block,
    fetch_city_block,
    fetch_state_block,
    fetch_zip_block,
)
from mpi.resolver.identification_resolver import fetch_identification_block
from mpi.resolver.patient_resolver import fetch_patients
from mpi.resolver.sex_resolver import fetch_current_sex_block
from mpi.resolver.phone_resolver import fetch_phone_block


def resolve_first_name(
    value: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_first_name_block(value, transform, connection)


def resolve_middle_name(
    value: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_middle_name_block(value, transform, connection)


def resolve_second_middle_name(
    value: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_second_middle_name_block(value, transform, connection)


def resolve_suffix(
    value: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_suffix_block(value, transform, connection)


def resolve_phone(
    value: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_phone_block(value, transform, connection)


def resolve_current_sex(value: str | None, connection: Connection) -> list[str]:
    return fetch_current_sex_block(value, connection)


def resolve_last_name(
    value: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_last_name_block(value, transform, connection)


def resolve_birthdate(date: str | None, connection: Connection) -> list[str]:
    return fetch_birthdate_block(date, connection)


def resolve_address(
    street: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_address_block(street, transform, connection)


def resolve_city(
    city: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_city_block(city, transform, connection)


def resolve_state(
    state: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_state_block(state, transform, connection)


def resolve_zip(
    zip: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_zip_block(zip, transform, connection)


def resolve_identification(
    value: str | None,
    identification_type: IdentificationType,
    transform: Transform | None,
    connection: Connection,
) -> list[str]:
    return fetch_identification_block(value, identification_type, transform, connection)


def resolve_patients(ids: set[int], connection: Connection) -> list[Patient]:
    return fetch_patients(ids, connection)
