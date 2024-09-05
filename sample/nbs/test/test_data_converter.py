from linkage.models.patient import System
from nbs.mpi.data_converter import (
    to_name,
    to_address,
    to_identifications,
    to_telecom_phone,
    to_telecom_email,
)

name_string = """
[
    {
        "lastNm": "Last",
        "lastNm2": "Last2",
        "middleNm": "Middle",
        "middleNm2": null,
        "firstNm": "First",
        "nmSuffix": "Jr",
        "nmUse": "L"
    },
    {
        "lastNm": "anotherLast",
        "lastNm2": null,
        "middleNm": "anotherMiddle",
        "middleNm2": null,
        "firstNm": "anotherFirst",
        "nmSuffix": null,
        "nmUse": null
    }
]
"""

address_string = """
[
    {
        "street1": "1234 Main st",
        "street2": null,
        "city": "Madisonville",
        "state": "TN",
        "zip": "37354"
    },
    {
        "street1": "2436 Pleasant Valley Rd",
        "street2": null,
        "city": "OWENSBORO",
        "state": "KY",
        "zip": "30309"
    }
]
"""

identification_string = """
[
    { "type": "SS", "assigningAuthority": "SSA", "value": "1234567890" },
    { "type": "AN", "assigningAuthority": "AK", "value": "4441" }
]
"""

phone_string = """
[
    {"value":"123-444-1111"},
    {"value":"123-123-1234"}]
"""

email_string = """
[
    {"value":"email@email.org"},
    {"value":"email@email.com"}
]
"""


def test_name_parse():
    name = to_name(name_string)
    assert name.family == "Last"
    assert name.given == ["First", "Middle"]
    assert name.suffix == "Jr"
    assert name.use == "L"


def test_addresss_parse():
    address = to_address(address_string)
    assert address.street == ["1234 Main st"]
    assert address.city == "Madisonville"
    assert address.zip == "37354"
    assert address.state == "TN"


def test_identification_parse():
    identifications = to_identifications(identification_string)
    assert identifications[0].type == "SS"
    assert identifications[0].assigning_authority == "SSA"
    assert identifications[0].value == "1234567890"

    assert identifications[1].type == "AN"
    assert identifications[1].assigning_authority == "AK"
    assert identifications[1].value == "4441"


def test_phone_parse():
    telecom = to_telecom_phone(phone_string)
    assert len(telecom) == 2
    assert telecom[0].system == System.PHONE
    assert telecom[0].value == "123-444-1111"

    assert telecom[1].system == System.PHONE
    assert telecom[1].value == "123-123-1234"


def test_email_parse():
    telecom = to_telecom_email(email_string)
    assert len(telecom) == 2
    assert telecom[0].system == System.EMAIL
    assert telecom[0].value == "email@email.org"

    assert telecom[1].system == System.EMAIL
    assert telecom[1].value == "email@email.com"
