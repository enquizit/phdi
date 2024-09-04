from enum import Enum
from dataclasses import dataclass


class Field(Enum):
    FIRST_NAME = "first_name"
    LAST_NAME = "last_name"
    MIDDLE_NAME = "middle_name"
    SECOND_MIDDLE_NAME = "second_middle_name"
    SUFFIX = "suffix"
    BIRTHDATE = "birthdate"
    CURRENT_SEX = "current_sex"
    TELEPHONE = "telephone"
    STREET_ADDRESS = "street_address"
    CITY = "city"
    STATE = "state"
    ZIP = "zip"
    IDENTIFICATION_MRN = "mrn"
    IDENTIFICATION_SSN = "ssn"


class Function(Enum):
    FIRST_FOUR = "first_four"
    EXACT_MATCH = "exact_match"
    LOG_ODDS_EXACT_MATCH = "log_odds_exact_match"
    FUZZY_MATCH = "fuzzy_match"
    LOG_ODDS_FUZZY_MATCH = "log_odds_fuzzy_match"


class Transform(Enum):
    FIRST_FOUR = "first_four"
    LAST_FOUR = "last_four"


class SimilarityMeasure(Enum):
    JAROWINKLER = "jarowinkler"


@dataclass
class BlockCriteria:
    field: Field
    transform: Transform | None


@dataclass
class Arguments:
    log_odds: dict[Field, float]
    field_thresholds: dict[Field, float]
    cluster_ratio: float
    true_match_threshold: float
    human_review_threshold: float | None
    similarity_measure: SimilarityMeasure | None


@dataclass
class Pass:
    functions: dict[Field, Function]
    blocks: list[BlockCriteria]
    args: Arguments


@dataclass
class Configuration:
    passes: list[Pass]
