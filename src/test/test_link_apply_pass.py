from linkage.link import (
    apply_pass,
)
from linkage.models.patient import Patient, Name, Address, Identification
from linkage.models.configuration import (
    Function,
    SimilarityMeasure,
    Pass,
    Field,
    Arguments,
)
from linkage.models.result import MatchType


# Test apply_pass
def test_apply_pass_single_function_exact():
    a = Patient(
        "2003-10-08",
        "M",
        Name("legal", "family", "suffix", ["first", "middle", "second-middle"]),
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        [
            Identification("MR", "12345", "mrn assigning authority"),
            Identification("SS", "123-45-6789", "ss assigning authority"),
        ],
    )
    b = Patient(
        "2003-10-08",
        "M",
        Name("legal", "family", "suffix", ["first", "middle", "second-middle"]),
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        [
            Identification("MR", "12345", "mrn assigning authority"),
            Identification("SS", "123-45-6789", "ss assigning authority"),
        ],
    )
    pass_config = Pass(
        {Field.FIRST_NAME: Function.EXACT_MATCH},
        [],
        Arguments(
            log_odds={Field.FIRST_NAME: 2},
            field_thresholds={Field.FIRST_NAME: 0.7},
            cluster_ratio=1,
            true_match_threshold=1,
            human_review_threshold=None,
            similarity_measure=SimilarityMeasure.JAROWINKLER,
        ),
    )
    pass_result = apply_pass(a, b, pass_config)
    assert pass_result.match_type == MatchType.EXACT


def test_apply_pass_single_function_review():
    a = Patient(
        "2003-10-08",
        "M",
        Name("legal", "family", "suffix", ["first", "middle", "second-middle"]),
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        [
            Identification("MR", "12345", "mrn assigning authority"),
            Identification("SS", "123-45-6789", "ss assigning authority"),
        ],
    )
    b = Patient(
        "2003-10-08",
        "M",
        Name("legal", "family", "suffix", ["first", "middle", "second-middle"]),
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        [
            Identification("MR", "12345", "mrn assigning authority"),
            Identification("SS", "123-45-6789", "ss assigning authority"),
        ],
    )
    pass_config = Pass(
        {Field.FIRST_NAME: Function.EXACT_MATCH},
        [],
        Arguments(
            log_odds={Field.FIRST_NAME: 2},
            field_thresholds={Field.FIRST_NAME: 0.7},
            cluster_ratio=1,
            true_match_threshold=1.5,
            human_review_threshold=1,
            similarity_measure=SimilarityMeasure.JAROWINKLER,
        ),
    )
    pass_result = apply_pass(a, b, pass_config)
    assert pass_result.match_type == MatchType.HUMAN_REVIEW


def test_apply_pass_single_function_none():
    a = Patient(
        "2003-10-08",
        "M",
        Name("legal", "family", "suffix", ["fiasd", "middle", "second-middle"]),
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        [
            Identification("MR", "12345", "mrn assigning authority"),
            Identification("SS", "123-45-6789", "ss assigning authority"),
        ],
    )
    b = Patient(
        "2003-10-08",
        "M",
        Name("legal", "family", "suffix", ["first", "middle", "second-middle"]),
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        [
            Identification("MR", "12345", "mrn assigning authority"),
            Identification("SS", "123-45-6789", "ss assigning authority"),
        ],
    )
    pass_config = Pass(
        {Field.FIRST_NAME: Function.LOG_ODDS_FUZZY_MATCH},
        [],
        Arguments(
            log_odds={Field.FIRST_NAME: 2},
            field_thresholds={Field.FIRST_NAME: 0.7},
            cluster_ratio=1,
            true_match_threshold=1.8,
            human_review_threshold=1.6,
            similarity_measure=SimilarityMeasure.JAROWINKLER,
        ),
    )
    pass_result = apply_pass(a, b, pass_config)
    assert pass_result.match_type == MatchType.NONE


def test_apply_pass_multi():
    a = Patient(
        "2003-10-08",
        "M",
        Name("legal", "last", "suffix", ["John", "middle", "second-middle"]),
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        [
            Identification("MR", "12345", "mrn assigning authority"),
            Identification("SS", "123-45-6789", "ss assigning authority"),
        ],
    )
    b = Patient(
        "2003-10-08",
        "M",
        Name("legal", "last", "suffix", ["Jon", "middle", "second-middle"]),
        Address("city_value", "state", "zip_value", ["street 1", "street 2"]),
        [
            Identification("MR", "12345", "mrn assigning authority"),
            Identification("SS", "123-45-6789", "ss assigning authority"),
        ],
    )
    pass_config = Pass(
        {
            Field.FIRST_NAME: Function.LOG_ODDS_FUZZY_MATCH,
            Field.LAST_NAME: Function.EXACT_MATCH,
        },
        [],
        Arguments(
            log_odds={Field.FIRST_NAME: 2, Field.LAST_NAME: 1},
            field_thresholds={Field.FIRST_NAME: 0.7, Field.LAST_NAME: 0.7},
            cluster_ratio=1,
            true_match_threshold=2.5,
            human_review_threshold=None,
            similarity_measure=SimilarityMeasure.JAROWINKLER,
        ),
    )
    pass_result = apply_pass(a, b, pass_config)
    assert pass_result.match_type == MatchType.EXACT
