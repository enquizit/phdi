from pyodbc import Connection
from linkage.models.configuration import Transform

query = """
        SELECT DISTINCT
            p.person_parent_uid
        FROM
            person_name pn
            JOIN person p ON p.person_uid = pn.person_uid
        WHERE
            pn.record_status_cd = 'ACTIVE'
            AND pn.nm_use_cd = 'L'
            AND p.cd = 'PAT'
            
        """


def fetch_first_name_block(
    name: str | None, transform: Transform | None, connection: Connection
) -> list[str]:

    match transform:
        case Transform.FIRST_FOUR:
            search_query = f"{query} AND LEFT(pn.first_nm, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            search_query = f"{query} AND RIGHT(pn.first_nm, 4) = RIGHT(?, 4)"
        case _:
            search_query = f"{query} AND pn.first_nm = ?"

    with connection.cursor() as cursor:
        cursor.execute(search_query, name)
        records = cursor.fetchall()
        return [r.person_parent_uid for r in records]


def fetch_middle_name_block(
    name: str | None, transform: Transform | None, connection: Connection
) -> list[str]:

    match transform:
        case Transform.FIRST_FOUR:
            search_query = f"{query} AND LEFT(pn.middle_nm, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            search_query = f"{query} AND RIGHT(pn.middle_nm, 4) = RIGHT(?, 4)"
        case _:
            search_query = f"{query} AND pn.middle_nm = ?"

    with connection.cursor() as cursor:
        cursor.execute(search_query, name)
        records = cursor.fetchall()
        return [r.person_parent_uid for r in records]


def fetch_second_middle_name_block(
    name: str | None, transform: Transform | None, connection: Connection
) -> list[str]:

    match transform:
        case Transform.FIRST_FOUR:
            search_query = f"{query} AND LEFT(pn.middle_nm2, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            search_query = f"{query} AND RIGHT(pn.middle_nm2, 4) = RIGHT(?, 4)"
        case _:
            search_query = f"{query} AND pn.middle_nm2 = ?"

    with connection.cursor() as cursor:
        cursor.execute(search_query, name)
        records = cursor.fetchall()
        return [r.person_parent_uid for r in records]


def fetch_last_name_block(
    name: str | None, transform: Transform | None, connection: Connection
) -> list[str]:

    match transform:
        case Transform.FIRST_FOUR:
            search_query = f"{query} AND LEFT(pn.last_nm, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            search_query = f"{query} AND RIGHT(pn.last_nm, 4) = RIGHT(?, 4)"
        case _:
            search_query = f"{query} AND pn.last_nm = ?"

    with connection.cursor() as cursor:
        cursor.execute(search_query, name)
        records = cursor.fetchall()
        return [r.person_parent_uid for r in records]
