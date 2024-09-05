from linkage.models.configuration import Field
from dataclasses import dataclass
from enum import Enum


class MatchType(Enum):
    EXACT = "EXACT"
    HUMAN_REVIEW = "HUMAN_REVIEW"
    NONE = "NONE"


@dataclass
class PassResult:
    patient: int
    field_scores: dict[Field, float]
    match_type: MatchType


@dataclass
class ClusterRatio:
    exact_match: float
    human_review: float
    pass_results: list[PassResult]


@dataclass
class LinkageScore:
    patient: int
    score: float
    match_type: MatchType
    cluster_ratio: ClusterRatio


@dataclass
class Response:
    patient: int | None
    match_type: MatchType
    linkage_scores: list[LinkageScore] | None
