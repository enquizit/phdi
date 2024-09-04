import pytest
from linkage.link import cluster_patients
from linkage.models.patient import Patient


# Test cluster patients
def test_cluster():
    patients = [
        Patient(
            None,
            None,
            None,
            None,
            [],
            [],
            "patient_id_1",
            "person_id_1",
        ),
        Patient(
            None,
            None,
            None,
            None,
            [],
            [],
            "patient_id_2",
            "person_id_1",
        ),
        Patient(
            None,
            None,
            None,
            None,
            [],
            [],
            "patient_id_3",
            "person_id_1",
        ),
        Patient(
            None,
            None,
            None,
            None,
            [],
            [],
            "patient_id_4",
            "person_id_2",
        ),
    ]
    clusters = cluster_patients(patients)
    assert len(clusters) == 2

    assert clusters["person_id_1"] is not None
    patient_ids = [p.patient_id for p in clusters["person_id_1"]]
    assert ["patient_id_1", "patient_id_2", "patient_id_3"] == patient_ids

    assert clusters["person_id_2"] is not None
    patient_ids = [p.patient_id for p in clusters["person_id_2"]]
    assert ["patient_id_4"] == patient_ids


def test_cluster_empty():
    patients = []
    clusters = cluster_patients(patients)
    assert len(clusters) == 0


def test_cluster_no_patient_id():
    patients = [
        Patient(
            None,
            None,
            None,
            None,
            [],
            [],
            "patient_id_4",
            None,
        ),
    ]
    with pytest.raises(ValueError):
        cluster_patients(patients)
