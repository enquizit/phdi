import pytest
from linkage.compare import (
    match_first_four,
    match_exact,
    match_exact_log_odds,
    match_fuzzy,
    match_fuzzy_log_odds,
)
from linkage.models.configuration import SimilarityMeasure


# First four
def test_match_first_four_no_match():
    score = match_first_four("first", "second")
    assert score == 0


def test_match_first_four_match():
    score = match_first_four("first", "firsb")
    assert score == 1


def test_match_first_four_none():
    score = match_first_four("first", None)
    assert score == 0


def test_match_first_four_none_none():
    score = match_first_four(None, None)
    assert score == 1


# Exact match
def test_match_exact_no_match():
    score = match_exact("first", "second")
    assert score == 0


def test_match_exact_match_extra():
    score = match_exact("first", "firsta")
    assert score == 0


def test_match_exact_match():
    score = match_exact("first", "first")
    assert score == 1


def test_match_exact_none():
    score = match_exact("first", None)
    assert score == 0


def test_match_exact_none_none():
    score = match_exact(None, None)
    assert score == 1


# Match exact log odds
def test_match_exact_log_odds_no_match():
    score = match_exact_log_odds("first", "second", 5.0)
    assert score == 0


def test_match_exact_match_extra():
    score = match_exact_log_odds("first", "firsta", 5.0)
    assert score == 0


def test_match_exact_match():
    score = match_exact_log_odds("first", "first", 5.0)
    assert score == 5


def test_match_exact_none():
    score = match_exact_log_odds("first", None, 5.0)
    assert score == 0


def test_match_exact_none_none():
    score = match_exact_log_odds(None, None, 5.0)
    assert score == 5


def test_match_exact_no_log_odd():
    with pytest.raises(ValueError):
        match_exact_log_odds(None, None, None)


# Match fuzzy
def test_fuzzy_no_match():
    score = match_fuzzy("first", "second", SimilarityMeasure.JAROWINKLER, 0.7)
    assert score == 0


def test_fuzzy_match():
    score = match_fuzzy("John", "Jon", SimilarityMeasure.JAROWINKLER, 0.7)
    assert score == 1


def test_fuzzy_match_exact_threshold():
    score = match_fuzzy("John", "John", SimilarityMeasure.JAROWINKLER, 1)
    assert score == 1


def test_fuzzy_none():
    score = match_fuzzy(None, None, SimilarityMeasure.JAROWINKLER, 0.7)
    assert score == 1


def test_fuzzy_no_measure():
    with pytest.raises(ValueError):
        match_fuzzy("a", "a", None, 0.7)


def test_fuzzy_no_threshold():
    with pytest.raises(ValueError):
        match_fuzzy("a", "a", SimilarityMeasure.JAROWINKLER, None)


def test_fuzzy_threshold_too_low():
    with pytest.raises(ValueError):
        match_fuzzy("a", "a", SimilarityMeasure.JAROWINKLER, -0.1)


def test_fuzzy_threshold_too_high():
    with pytest.raises(ValueError):
        match_fuzzy("a", "a", SimilarityMeasure.JAROWINKLER, 1.1)


# Match fuzzy log odds
def test_match_fuzzy_log_odds_no_match():
    score = match_fuzzy_log_odds(
        "first", "second", 2, SimilarityMeasure.JAROWINKLER, 0.7
    )
    assert score == 0


def test_match_fuzzy_log_odds_match():
    score = match_fuzzy_log_odds("John", "John", 2, SimilarityMeasure.JAROWINKLER, 0.7)
    assert score == 2


def test_match_fuzzy_log_odds_exact_threshold():
    score = match_fuzzy_log_odds("John", "John", 2, SimilarityMeasure.JAROWINKLER, 1)
    assert score == 2


def test_match_fuzzy_log_odds_close_match():
    score = match_fuzzy_log_odds("John", "Jon", 2, SimilarityMeasure.JAROWINKLER, 0.7)
    assert score > 1 and score < 2


def test_match_fuzzy_log_odds_none():
    score = match_fuzzy_log_odds(None, None, 2, SimilarityMeasure.JAROWINKLER, 0.7)
    assert score == 2


def test_match_fuzzy_log_odds_no_measure():
    with pytest.raises(ValueError):
        match_fuzzy_log_odds("a", "a", 2, None, 0.7)


def test_match_fuzzy_log_odds_no_threshold():
    with pytest.raises(ValueError):
        match_fuzzy_log_odds("a", "a", 2, SimilarityMeasure.JAROWINKLER, None)


def test_match_fuzzy_log_odds_threshold_too_low():
    with pytest.raises(ValueError):
        match_fuzzy_log_odds("a", "a", 2, SimilarityMeasure.JAROWINKLER, -0.1)


def test_match_fuzzy_log_odds_threshold_too_high():
    with pytest.raises(ValueError):
        match_fuzzy_log_odds("a", "a", 2, SimilarityMeasure.JAROWINKLER, 1.1)
