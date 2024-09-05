from linkage.models.configuration import BlockCriteria, Transform, Field
from linkage.models.identification_types import IdentificationType
from dataclasses import dataclass
from datetime import datetime


@dataclass
class SearchQuery:
    query: str
    params: list[str | None]


def map_to_query(field_value: str, criteria: BlockCriteria) -> SearchQuery:
    match criteria.field:
        case Field.FIRST_NAME:
            return generate_first_name_query(field_value, criteria.transform)
        case Field.MIDDLE_NAME:
            return generate_middle_name_query(field_value, criteria.transform)
        case Field.SECOND_MIDDLE_NAME:
            return generate_second_middle_name_query(field_value, criteria.transform)
        case Field.LAST_NAME:
            return generate_last_name_query(field_value, criteria.transform)
        case Field.SUFFIX:
            return generate_suffix_query(field_value, criteria.transform)
        case Field.CURRENT_SEX:
            # transform not currently supported
            return generate_current_sex_query(field_value)
        case Field.BIRTHDATE:
            # transform not currently supported
            return generate_birthdate_query(field_value)
        case Field.TELEPHONE:
            return generate_phone_query(field_value, criteria.transform)
        case Field.STREET_ADDRESS:
            return generate_address_query(field_value, criteria.transform)
        case Field.CITY:
            return generate_city_query(field_value, criteria.transform)
        case Field.STATE:
            return generate_state_query(field_value, criteria.transform)
        case Field.ZIP:
            return generate_zip_query(field_value, criteria.transform)
        case Field.IDENTIFICATION_MRN:
            return generate_identification_query(
                field_value,
                criteria.transform,
                IdentificationType.MEDICAL_RECORD_NUMBER,
            )
        case Field.IDENTIFICATION_SSN:
            return generate_identification_query(
                field_value,
                criteria.transform,
                IdentificationType.SOCIAL_SECURITY_NUMBER,
            )


def generate_first_name_query(
    field_value: str | None, transform: Transform | None
) -> SearchQuery:
    match transform:
        case Transform.FIRST_FOUR:
            query_string = " AND LEFT(pn.first_nm, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            query_string = " AND RIGHT(pn.first_nm, 4) = RIGHT(?, 4)"
        case _:
            query_string = " AND pn.first_nm = ?"
    return SearchQuery(query_string, [field_value])


def generate_middle_name_query(
    field_value: str | None, transform: Transform | None
) -> SearchQuery:
    match transform:
        case Transform.FIRST_FOUR:
            query_string = " AND LEFT(pn.middle_nm, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            query_string = " AND RIGHT(pn.middle_nm, 4) = RIGHT(?, 4)"
        case _:
            query_string = " AND pn.middle_nm = ?"
    return SearchQuery(query_string, [field_value])


def generate_second_middle_name_query(
    field_value: str | None, transform: Transform | None
) -> SearchQuery:
    match transform:
        case Transform.FIRST_FOUR:
            query_string = " AND LEFT(pn.middle_nm2, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            query_string = " AND RIGHT(pn.middle_nm2, 4) = RIGHT(?, 4)"
        case _:
            query_string = " AND pn.middle_nm2 = ?"
    return SearchQuery(query_string, [field_value])


def generate_last_name_query(
    field_value: str | None, transform: Transform | None
) -> SearchQuery:
    match transform:
        case Transform.FIRST_FOUR:
            query_string = " AND LEFT(pn.last_nm, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            query_string = " AND RIGHT(pn.last_nm, 4) = RIGHT(?, 4)"
        case _:
            query_string = " AND pn.last_nm = ?"
    return SearchQuery(query_string, [field_value])


def generate_suffix_query(
    field_value: str | None, transform: Transform | None
) -> SearchQuery:
    match transform:
        case Transform.FIRST_FOUR:
            query_string = " AND LEFT(pn.nm_suffix, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            query_string = " AND RIGHT(pn.nm_suffix, 4) = RIGHT(?, 4)"
        case _:
            query_string = " AND pn.nm_suffix = ?"
    return SearchQuery(query_string, [field_value])


def generate_current_sex_query(field_value: str | None) -> SearchQuery:
    query_string = " AND p.curr_sex_cd = ?"
    return SearchQuery(query_string, [field_value])


def generate_birthdate_query(field_value: str | None) -> SearchQuery:
    query_string = """
         AND year(birth_time) = ?
        AND month(birth_time) = ?
        AND day(birth_time) = ?
        AND birth_time IS NOT NULL
    """
    if field_value is None:
        return SearchQuery(query_string, [None, None, None])
    parsed_date = datetime.strptime(field_value, "%Y-%m-%d")
    return SearchQuery(
        query_string, [parsed_date.year, parsed_date.month, parsed_date.day]
    )


def generate_address_query(
    field_value: str | None, transform: Transform | None
) -> SearchQuery:
    match transform:
        case Transform.FIRST_FOUR:
            query_string = " AND LEFT(pl.street_addr1, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            query_string = " AND RIGHT(pl.street_addr1, 4) = RIGHT(?, 4)"
        case _:
            query_string = " AND pl.street_addr1 = ?"
    return SearchQuery(query_string, [field_value])


def generate_city_query(
    field_value: str | None, transform: Transform | None
) -> SearchQuery:
    match transform:
        case Transform.FIRST_FOUR:
            query_string = " AND LEFT(pl.city_desc_txt, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            query_string = " AND RIGHT(pl.city_desc_txt, 4) = RIGHT(?, 4)"
        case _:
            query_string = " AND pl.city_desc_txt = ?"
    return SearchQuery(query_string, [field_value])


def generate_state_query(
    field_value: str | None, transform: Transform | None
) -> SearchQuery:
    match transform:
        case Transform.FIRST_FOUR:
            query_string = " AND LEFT(pl.state_cd, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            query_string = " AND RIGHT(pl.state_cd, 4) = RIGHT(?, 4)"
        case _:
            query_string = " AND pl.state_cd = ?"
    return SearchQuery(query_string, [field_value])


def generate_zip_query(
    field_value: str | None, transform: Transform | None
) -> SearchQuery:
    match transform:
        case Transform.FIRST_FOUR:
            query_string = " AND LEFT(pl.zip_cd, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            query_string = " AND RIGHT(pl.zip_cd, 4) = RIGHT(?, 4)"
        case _:
            query_string = " AND pl.zip_cd = ?"
    return SearchQuery(query_string, [field_value])


def generate_phone_query(
    field_value: str | None, transform: Transform | None
) -> SearchQuery:
    match transform:
        case Transform.FIRST_FOUR:
            query_string = " AND LEFT(tl.phone_nbr_txt, 4) = LEFT(?, 4)"
        case Transform.LAST_FOUR:
            query_string = " AND RIGHT(tl.phone_nbr_txt, 4) = RIGHT(?, 4)"
        case _:
            query_string = " AND tl.phone_nbr_txt = ?"
    return SearchQuery(query_string, [field_value])


def generate_identification_query(
    field_value: str | None,
    transform: Transform | None,
    identification_type: IdentificationType,
) -> SearchQuery:
    match transform:
        case Transform.FIRST_FOUR:
            query_string = (
                " AND id.type_cd = ? AND LEFT(id.root_extension_txt, 4) = LEFT(?, 4)"
            )
        case Transform.LAST_FOUR:
            query_string = (
                " AND id.type_cd = ? AND RIGHT(id.root_extension_txt, 4) = RIGHT(?, 4)"
            )
        case _:
            query_string = " AND id.type_cd = ? AND id.root_extension_txt = ?"
    return SearchQuery(query_string, [identification_type.value, field_value])
