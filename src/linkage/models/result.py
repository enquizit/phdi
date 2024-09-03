from dataclasses import dataclass
from enum import Enum


class MatchType(Enum):
    EXACT = "EXACT"
    HUMAN_REVIEW = "HUMAN_REVIEW"
    NONE = "NONE"


@dataclass
class ClusterRatio:
    exact_match: float
    human_review: float


@dataclass
class Result:
    patient: int | None
    cluster_ratio: float
    match_type: MatchType
