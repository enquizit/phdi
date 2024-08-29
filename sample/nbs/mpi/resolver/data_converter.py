import json
from linkage.models.patient import Name, Address, Identification


def to_name(json_string: str | None) -> Name | None:
    if json_string is None:
        return None
    json_object = json.loads(json_string)
    if len(json_object) == 0:
        return None
    given_names = [
        _get_value("firstNm", json_object[0]),
        _get_value("middleNm", json_object[0]),
        _get_value("middleNm2", json_object[0]),
    ]
    return Name(
        _get_value("nmUse", json_object[0]),
        _get_value("lastNm", json_object[0]),
        _get_value("nmSuffix", json_object[0]),
        [x for x in given_names if x is not None],
    )


def to_address(json_string: str | None) -> Address | None:
    if json_string is None:
        return None
    json_object = json.loads(json_string)
    if len(json_object) == 0:
        return None
    street_addresses = [
        _get_value("street1", json_object[0]),
        _get_value("street2", json_object[0]),
    ]
    return Address(
        _get_value("city", json_object[0]),
        _get_value("state", json_object[0]),
        _get_value("zip", json_object[0]),
        [x for x in street_addresses if x is not None],
    )


def to_identifications(json_string: str | None) -> list[Identification]:
    if json_string is None:
        return []
    json_object = json.loads(json_string)
    if len(json_object) == 0:
        return []
    return [_to_identification(x) for x in json_object if x is not None]


def _to_identification(identification_dict: dict[str, str]) -> Identification:
    return Identification(
        _get_value("type", identification_dict),
        _get_value("value", identification_dict),
        _get_value("assigningAuthority", identification_dict),
    )


def _get_value(key: str, json_object: dict[str, str] | None) -> str | None:
    return json_object[key] if json_object is not None and key in json_object else None
