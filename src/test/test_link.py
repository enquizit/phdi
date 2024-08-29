from linkage.models.client import BaseMPIConnectorClient
from linkage.link import link_record
from linkage.models.patient import Patient, Name
from linkage.models.configuration import (
    Configuration,
    Function,
    SimilarityMeasure,
    Pass,
    Field,
    Arguments,
    BlockCriteria,
)


class TestMpiClient(BaseMPIConnectorClient):
    __test__ = False

    expected_patient: Patient
    expected_criteria: list[BlockCriteria]
    response: list[Patient]

    def __init__(
        self,
        expected_patient: Patient,
        expected_criteria: list[BlockCriteria],
        response: list[Patient],
    ):
        self.expected_patient = expected_patient
        self.expected_criteria = expected_criteria
        self.response = response

    def get_patient_data(
        self, patient: Patient, criteria: list[BlockCriteria]
    ) -> list[Patient]:
        assert patient is not None
        assert self.expected_patient == patient
        assert self.expected_criteria == criteria
        return self.response


# Test link
def test_link_match():
    blocking_criteria = [BlockCriteria(Field.LAST_NAME, None)]
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
            "patient_id_1",
            "person_id_1",
        ),
        Patient(
            None,
            None,
            Name("legal", "Doe", "suffix", ["Johnathan", "middle", "second-middle"]),
            None,
            [],
            "patient_id_2",
            "person_id_1",
        ),
        Patient(
            None,
            None,
            Name("legal", "Smith", "suffix", ["Barbara", "middle", "second-middle"]),
            None,
            [],
            "patient_id_3",
            "person_id_2",
        ),
    ]
    link_results = link_record(
        patient,
        Configuration(
            [
                Pass(
                    {
                        Field.FIRST_NAME: Function.LOG_ODDS_FUZZY_MATCH,
                        Field.LAST_NAME: Function.EXACT_MATCH,
                    },
                    blocking_criteria,
                    Arguments(
                        log_odds={Field.FIRST_NAME: 2, Field.LAST_NAME: 1},
                        field_thresholds={Field.FIRST_NAME: 0.7, Field.LAST_NAME: 0.7},
                        cluster_ratio=0.6,
                        true_match_threshold=2.5,
                        human_review_threshold=None,
                        similarity_measure=SimilarityMeasure.JAROWINKLER,
                    ),
                )
            ]
        ),
        TestMpiClient(patient, blocking_criteria, existing_patients),
    )
    assert link_results == "person_id_1"


def test_link_no_match():
    blocking_criteria = [BlockCriteria(Field.LAST_NAME, None)]
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
            "patient_id_3",
            "person_id_2",
        ),
    ]
    link_results = link_record(
        patient,
        Configuration(
            [
                Pass(
                    {
                        Field.FIRST_NAME: Function.LOG_ODDS_FUZZY_MATCH,
                        Field.LAST_NAME: Function.EXACT_MATCH,
                    },
                    blocking_criteria,
                    Arguments(
                        log_odds={Field.FIRST_NAME: 2, Field.LAST_NAME: 1},
                        field_thresholds={Field.FIRST_NAME: 0.7, Field.LAST_NAME: 0.7},
                        cluster_ratio=0.6,
                        true_match_threshold=2.5,
                        human_review_threshold=None,
                        similarity_measure=SimilarityMeasure.JAROWINKLER,
                    ),
                )
            ]
        ),
        TestMpiClient(patient, blocking_criteria, existing_patients),
    )
    assert link_results is None
