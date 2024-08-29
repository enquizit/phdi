from pyodbc import Connection
from linkage.models.configuration import Transform
from linkage.models.identification_types import IdentificationType

query = """
    SELECT DISTINCT
        p.person_parent_uid
    FROM
        entity_id id
        JOIN person p ON p.person_uid = id.entity_uid
    WHERE
        id.record_status_cd = 'ACTIVE'
        AND id.type_cd = ?
        AND p.cd = 'PAT'

        """


def fetch_identification_block(
    value: str | None,
    identification_type: IdentificationType,
    transform: Transform | None,
    connection: Connection,
) -> list[str]:

    match transform:
        case Transform.FIRST_FOUR:
            search_query = f"{query} AND LEFT(id.root_extension_txt, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            search_query = f"{query} AND RIGHT(id.root_extension_txt, 4) = RIGHT(?, 4)"
        case _:
            search_query = f"{query} AND id.root_extension_txt = ?"

    with connection.cursor() as cursor:
        cursor.execute(search_query, identification_type, value)
        records = cursor.fetchall()
        return [r.person_parent_uid for r in records]
