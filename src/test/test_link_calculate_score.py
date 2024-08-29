from linkage.link import calculate_score
from linkage.models.configuration import (
    Function,
    SimilarityMeasure,
)


# Test calculate_score
def test_calculate_score_first_four_no_match():
    score = calculate_score("first", "second", Function.FIRST_FOUR, None, None, None)
    assert score == 0


def test_calculate_score_first_four_match():
    score = calculate_score("first", "firsb", Function.FIRST_FOUR, None, None, None)
    assert score == 1


def test_calculate_score_exact_match():
    score = calculate_score("first", "first", Function.EXACT_MATCH, None, None, None)
    assert score == 1


def test_calculate_score_exact_no_match():
    score = calculate_score("first", "firstt", Function.EXACT_MATCH, None, None, None)
    assert score == 0


def test_calculate_score_log_odds_exact_match():
    score = calculate_score(
        "first", "first", Function.LOG_ODDS_EXACT_MATCH, None, 2, None
    )
    assert score == 2


def test_calculate_score_log_odds_exact_no_match():
    score = calculate_score(
        "first", "firstt", Function.LOG_ODDS_EXACT_MATCH, None, 0.6, None
    )
    assert score == 0


def test_calculate_score_fuzzy_match():
    score = calculate_score(
        "first", "first", Function.FUZZY_MATCH, 0.5, 2, SimilarityMeasure.JAROWINKLER
    )
    assert score == 1


def test_calculate_score_fuzzy_no_match():
    score = calculate_score(
        "first", "second", Function.FUZZY_MATCH, 0.5, 0.6, SimilarityMeasure.JAROWINKLER
    )
    assert score == 0


def test_calculate_score_fuzzy_log_odds_match():
    score = calculate_score(
        "first",
        "first",
        Function.LOG_ODDS_FUZZY_MATCH,
        0.5,
        2,
        SimilarityMeasure.JAROWINKLER,
    )
    assert score == 2


def test_calculate_score_fuzzy_log_odds_no_match():
    score = calculate_score(
        "first",
        "second",
        Function.LOG_ODDS_FUZZY_MATCH,
        0.5,
        0.6,
        SimilarityMeasure.JAROWINKLER,
    )
    assert score == 0
