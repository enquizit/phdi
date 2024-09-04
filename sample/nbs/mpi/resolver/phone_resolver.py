from pyodbc import Connection
from linkage.models.configuration import Transform

query = """
    SELECT DISTINCT
        p.person_parent_uid
    FROM
        tele_locator tl
        JOIN Entity_locator_participation elp ON elp.locator_uid = tl.tele_locator_uid
        JOIN person p ON elp.entity_uid = p.person_uid
    WHERE
        elp.record_status_cd = 'ACTIVE'
        AND elp.class_cd = 'TELE'
        AND p.cd = 'PAT'

        """


def fetch_phone_block(
    phone: str | None, transform: Transform | None, connection: Connection
) -> list[str]:
    if phone is None:
        return []
    match transform:
        case Transform.FIRST_FOUR:
            search_query = f"{query} AND LEFT(tl.phone_nbr_txt, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            search_query = f"{query} AND RIGHT(tl.phone_nbr_txt, 4) = RIGHT(?, 4)"
        case _:
            search_query = f"{query} AND tl.phone_nbr_txt = ?"

    with connection.cursor() as cursor:
        cursor.execute(search_query, phone)
        records = cursor.fetchall()
        return [r.person_parent_uid for r in records]
