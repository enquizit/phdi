from datetime import datetime
from pyodbc import Connection

query = """
    SELECT DISTINCT
        person_parent_uid
    FROM
        person
    WHERE
        record_status_cd = 'ACTIVE'
        AND cd = 'PAT'
        AND year(birth_time) = ?
        AND month(birth_time) = ?
        AND day(birth_time) = ?
        AND birth_time IS NOT NULL
        """


def fetch_birthdate_block(date: str | None, connection: Connection) -> list[str]:
    if date is None:
        return []
    parsed_date = datetime.strptime(date, "%Y-%m-%d")
    with connection.cursor() as cursor:
        cursor.execute(query, parsed_date.year, parsed_date.month, parsed_date.day)
        records = cursor.fetchall()
        return [r.person_parent_uid for r in records]
