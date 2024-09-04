from linkage.models.patient import Patient, Name, Identification, Address
from linkage.models.configuration import BlockCriteria, Transform, Field
from linkage.block import get_block_value, transform_value, transform_identification


def test_transform_first_four():
    value = transform_value("12345", Transform.FIRST_FOUR)
    assert value == "1234"


def test_transform_first_four_short():
    value = transform_value("123", Transform.FIRST_FOUR)
    assert value == "123"


def test_transform_first_four_empty():
    value = transform_value("", Transform.FIRST_FOUR)
    assert value is None


def test_transform_first_four_whitespace():
    value = transform_value("    ", Transform.FIRST_FOUR)
    assert value is None


def test_transform_last_four():
    value = transform_value("12345", Transform.LAST_FOUR)
    assert value == "2345"


def test_transform_last_four_short():
    value = transform_value("345", Transform.LAST_FOUR)
    assert value == "345"


def test_transform_value_none():
    value = transform_value(None, Transform.FIRST_FOUR)
    assert value is None


def test_transform_transform_none():
    value = transform_value("12345", None)
    assert value == "12345"


def test_transform_identification_first_four():
    mrn = Identification("MR", "12345", "assigning authority")
    value = transform_identification(mrn, Transform.FIRST_FOUR)
    assert value == "1234"


def test_transform_identification_first_four_short():
    mrn = Identification("MR", "123", "assigning authority")
    value = transform_identification(mrn, Transform.FIRST_FOUR)
    assert value == "123"


def test_transform_identification_last_four():
    mrn = Identification("MR", "12345", "assigning authority")
    value = transform_identification(mrn, Transform.LAST_FOUR)
    assert value == "2345"


def test_transform_identification_last_four_short():
    mrn = Identification("MR", "345", "assigning authority")
    value = transform_identification(mrn, Transform.LAST_FOUR)
    assert value == "345"


def test_transform_identification_none():
    mrn = Identification("MR", "12345", "assigning authority")
    value = transform_identification(mrn, None)
    assert value == "12345"


def test_transform_identification_none():
    value = transform_identification(None, None)
    assert value is None


def test_transform_identification_value_is_none():
    mrn = Identification("MR", None, "assigning authority")
    value = transform_identification(mrn, None)
    assert value is None


# First name
def test_first_name_transform_none():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-middle"]),
        None,
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.FIRST_NAME, None))
    assert value == "first"


def test_first_name_transform_first_four():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-middle"]),
        None,
        None,
    )
    value = get_block_value(
        patient, BlockCriteria(Field.FIRST_NAME, Transform.FIRST_FOUR)
    )
    assert value == "firs"


def test_first_name_transform_last_four():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-middle"]),
        None,
        None,
    )
    value = get_block_value(
        patient, BlockCriteria(Field.FIRST_NAME, Transform.LAST_FOUR)
    )
    assert value == "irst"


# Middle name
def test_middle_name_transform_none():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-middle"]),
        None,
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.MIDDLE_NAME, None))
    assert value == "middle"


def test_middle_name_transform_first_four():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-middle"]),
        None,
        None,
    )
    value = get_block_value(
        patient, BlockCriteria(Field.MIDDLE_NAME, Transform.FIRST_FOUR)
    )
    assert value == "midd"


def test_middle_name_transform_last_four():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-middle"]),
        None,
        None,
    )
    value = get_block_value(
        patient, BlockCriteria(Field.MIDDLE_NAME, Transform.LAST_FOUR)
    )
    assert value == "ddle"


# Second middle name
def test_second_middle_name_transform_none():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-middle"]),
        None,
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.SECOND_MIDDLE_NAME, None))
    assert value == "second-middle"


def test_second_middle_name_transform_first_four():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-middle"]),
        None,
        None,
    )
    value = get_block_value(
        patient, BlockCriteria(Field.SECOND_MIDDLE_NAME, Transform.FIRST_FOUR)
    )
    assert value == "seco"


def test_second_middle_name_transform_last_four():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-mid"]),
        None,
        None,
    )
    value = get_block_value(
        patient, BlockCriteria(Field.SECOND_MIDDLE_NAME, Transform.LAST_FOUR)
    )
    assert value == "-mid"


# last name
def test_last_name_transform_none():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-middle"]),
        None,
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.LAST_NAME, None))
    assert value == "family"


def test_last_name_transform_first_four():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-middle"]),
        None,
        None,
    )
    value = get_block_value(
        patient, BlockCriteria(Field.LAST_NAME, Transform.FIRST_FOUR)
    )
    assert value == "fami"


def test_last_name_transform_last_four():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-middle"]),
        None,
        None,
    )
    value = get_block_value(
        patient, BlockCriteria(Field.LAST_NAME, Transform.LAST_FOUR)
    )
    assert value == "mily"


# suffix
def test_suffix_transform_none():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-middle"]),
        None,
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.SUFFIX, None))
    assert value == "suffix"


def test_suffix_transform_first_four():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-middle"]),
        None,
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.SUFFIX, Transform.FIRST_FOUR))
    assert value == "suff"


def test_suffix_transform_last_four():
    patient = Patient(
        None,
        None,
        Name("", "family", "suffix", ["first", "middle", "second-middle"]),
        None,
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.SUFFIX, Transform.LAST_FOUR))
    assert value == "ffix"


# birth date
def test_birthdate_transform_none():
    patient = Patient(
        "2003-10-08",
        None,
        None,
        None,
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.BIRTHDATE, None))
    assert value == "2003-10-08"


def test_birthdate_transform_first_four():
    patient = Patient(
        "2003-10-08",
        None,
        None,
        None,
        None,
    )
    value = get_block_value(
        patient, BlockCriteria(Field.BIRTHDATE, Transform.FIRST_FOUR)
    )
    assert value == "2003"


def test_birthdate_transform_last_four():
    patient = Patient(
        "2003-10-08",
        None,
        None,
        None,
        None,
    )
    value = get_block_value(
        patient, BlockCriteria(Field.BIRTHDATE, Transform.LAST_FOUR)
    )
    assert value == "0-08"


# Street address
def test_street_transform_none():
    patient = Patient(
        None,
        None,
        None,
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.STREET_ADDRESS, None))
    assert value == "street 1"


def test_street_transform_first_four():
    patient = Patient(
        None,
        None,
        None,
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        None,
    )
    value = get_block_value(
        patient, BlockCriteria(Field.STREET_ADDRESS, Transform.FIRST_FOUR)
    )
    assert value == "stre"


def test_street_transform_last_four():
    patient = Patient(
        None,
        None,
        None,
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        None,
    )
    value = get_block_value(
        patient, BlockCriteria(Field.STREET_ADDRESS, Transform.LAST_FOUR)
    )
    assert value == "et 1"


# City
def test_city_transform_none():
    patient = Patient(
        None,
        None,
        None,
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.CITY, None))
    assert value == "city_value"


def test_city_transform_first_four():
    patient = Patient(
        None,
        None,
        None,
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.CITY, Transform.FIRST_FOUR))
    assert value == "city"


def test_city_transform_last_four():
    patient = Patient(
        None,
        None,
        None,
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.CITY, Transform.LAST_FOUR))
    assert value == "alue"


# State
def test_state_transform_none():
    patient = Patient(
        None,
        None,
        None,
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.STATE, None))
    assert value == "state"


def test_state_transform_first_four():
    patient = Patient(
        None,
        None,
        None,
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.STATE, Transform.FIRST_FOUR))
    assert value == "stat"


def test_state_transform_last_four():
    patient = Patient(
        None,
        None,
        None,
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.STATE, Transform.LAST_FOUR))
    assert value == "tate"


# Zip
def test_zip_transform_none():
    patient = Patient(
        None,
        None,
        None,
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.ZIP, None))
    assert value == "zip_value"


def test_zip_transform_first_four():
    patient = Patient(
        None,
        None,
        None,
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.ZIP, Transform.FIRST_FOUR))
    assert value == "zip_"


def test_zip_transform_last_four():
    patient = Patient(
        None,
        None,
        None,
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        None,
    )
    value = get_block_value(patient, BlockCriteria(Field.ZIP, Transform.LAST_FOUR))
    assert value == "alue"
