from pyodbc import Connection
from linkage.models.configuration import Transform
from linkage.models.patient import Patient
from linkage.models.identification_types import IdentificationType
from mpi.resolver.name_resolver import (
    fetch_first_name_block,
    fetch_last_name_block,
    fetch_middle_name_block,
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


def resolve_first_name(
    value: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_first_name_block(value, transform, connection)


def resolve_middle_name(
    value: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_middle_name_block(value, transform, connection)


def resolve_last_name(
    value: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_last_name_block(value, transform, connection)


def fetch_birthdate_block(date: str | None, connection: Connection) -> list[str]:
    return fetch_birthdate_block(date, connection)


def fetch_address_block(
    street: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_address_block(street, transform, connection)


def fetch_city_block(
    city: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_city_block(city, transform, connection)


def fetch_state_block(
    state: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_state_block(state, transform, connection)


def fetch_zip_block(
    zip: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    return fetch_zip_block(zip, transform, connection)


def fetch_identification_block(
    value: str | None,
    identification_type: IdentificationType,
    transform: Transform | None,
    connection: Connection,
) -> list[str]:
    return fetch_identification_block(value, identification_type, transform, connection)


def resolve_patients(ids: set[int]) -> list[Patient]:
    return fetch_patients(ids)
