from dataclasses import dataclass
from enum import Enum


class MatchType(Enum):
    EXACT = "EXACT"
    HUMAN_REVIEW = "HUMAN_REVIEW"
    NONE = "NONE"


@dataclass
class MatchResponse:
    patient: int | None
    matchType: MatchType
