from pyodbc import Connection
from linkage.models.configuration import Transform

query = """
    SELECT DISTINCT
        person_parent_uid
    FROM
        person
    WHERE
        record_status_cd = 'ACTIVE'
        AND cd = 'PAT'
        AND curr_sex_cd = ?
        """


def fetch_current_sex_block(sex: str | None, connection: Connection) -> list[str]:

    with connection.cursor() as cursor:
        cursor.execute(query, sex)
        records = cursor.fetchall()
        return [r.person_parent_uid for r in records]
