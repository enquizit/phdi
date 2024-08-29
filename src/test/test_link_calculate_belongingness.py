from linkage.link import calculate_belongingness
from linkage.models.patient import Patient, Name
from linkage.models.configuration import (
    Function,
    SimilarityMeasure,
    Pass,
    Field,
    Arguments,
)


# Test calculate_belongingness
def test_calculate_belongingness_two_thirds():
    patient = Patient(
        None,
        None,
        Name("legal", "Doe", "suffix", ["John", "middle", "second-middle"]),
        None,
        [],
    )
    existing_patients = [
        Patient(
            None,
            None,
            Name("legal", "Doe", "suffix", ["Jon", "middle", "second-middle"]),
            None,
            [],
        ),
        Patient(
            None,
            None,
            Name("legal", "Doe", "suffix", ["Johnathan", "middle", "second-middle"]),
            None,
            [],
        ),
        Patient(
            None,
            None,
            Name("legal", "Smith", "suffix", ["Barbara", "middle", "second-middle"]),
            None,
            [],
        ),
    ]
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
    belongingness = calculate_belongingness(patient, existing_patients, pass_config)
    assert belongingness == (2 / 3)


def test_calculate_belongingness_zero():
    patient = Patient(
        None,
        None,
        Name("legal", "Doe", "suffix", ["John", "middle", "second-middle"]),
        None,
        [],
    )
    existing_patients = [
        Patient(
            None,
            None,
            Name("legal", "Smith", "suffix", ["Barbara", "middle", "second-middle"]),
            None,
            [],
        ),
    ]
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
    belongingness = calculate_belongingness(patient, existing_patients, pass_config)
    assert belongingness == 0


def test_calculate_belongingness_one():
    patient = Patient(
        None,
        None,
        Name("legal", "Doe", "suffix", ["John", "middle", "second-middle"]),
        None,
        [],
    )
    existing_patients = [
        Patient(
            None,
            None,
            Name("legal", "Doe", "suffix", ["Jo", "middle", "second-middle"]),
            None,
            [],
        ),
    ]
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
    belongingness = calculate_belongingness(patient, existing_patients, pass_config)
    assert belongingness == 1
