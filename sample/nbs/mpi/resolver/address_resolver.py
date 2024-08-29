from pyodbc import Connection
from linkage.models.configuration import Transform

query = """
    SELECT DISTINCT
        p.person_parent_uid
    FROM
        postal_locator pl
        JOIN Entity_locator_participation elp ON elp.locator_uid = pl.postal_locator_uid
        JOIN person p ON elp.entity_uid = p.person_uid
    WHERE
        elp.record_status_cd = 'ACTIVE'
        AND p.cd = 'PAT'

        """


def fetch_address_block(
    street: str | None, transform: Transform | None, connection: Connection
) -> list[str]:

    match transform:
        case Transform.FIRST_FOUR:
            search_query = f"{query} AND LEFT(pl.street_addr1, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            search_query = f"{query} AND RIGHT(pl.street_addr1, 4) = RIGHT(?, 4)"
        case _:
            search_query = f"{query} AND pl.street_addr1 = ?"

    with connection.cursor() as cursor:
        cursor.execute(search_query, street)
        records = cursor.fetchall()
        return [r.person_parent_uid for r in records]


def fetch_city_block(
    city: str | None, transform: Transform | None, connection: Connection
) -> list[str]:

    match transform:
        case Transform.FIRST_FOUR:
            search_query = f"{query} AND LEFT(pl.city_desc_txt, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            search_query = f"{query} AND RIGHT(pl.city_desc_txt, 4) = RIGHT(?, 4)"
        case _:
            search_query = f"{query} AND pl.city_desc_txt = ?"

    with connection.cursor() as cursor:
        cursor.execute(search_query, city)
        records = cursor.fetchall()
        return [r.person_parent_uid for r in records]


def fetch_state_block(
    state: str | None, transform: Transform | None, connection: Connection
) -> list[str]:

    match transform:
        case Transform.FIRST_FOUR:
            search_query = f"{query} AND LEFT(pl.state_cd, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            search_query = f"{query} AND RIGHT(pl.state_cd, 4) = RIGHT(?, 4)"
        case _:
            search_query = f"{query} AND pl.state_cd = ?"

    with connection.cursor() as cursor:
        cursor.execute(search_query, state)
        records = cursor.fetchall()
        return [r.person_parent_uid for r in records]


def fetch_zip_block(
    zip: str | None, transform: Transform | None, connection: Connection
) -> list[str]:

    match transform:
        case Transform.FIRST_FOUR:
            search_query = f"{query} AND LEFT(pl.zip_cd, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            search_query = f"{query} AND RIGHT(pl.zip_cd, 4) = RIGHT(?, 4)"
        case _:
            search_query = f"{query} AND pl.zip_cd = ?"

    with connection.cursor() as cursor:
        cursor.execute(search_query, zip)
        records = cursor.fetchall()
        return [r.person_parent_uid for r in records]
