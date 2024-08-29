from linkage.models.patient import Patient
from linkage.models.configuration import (
    Configuration,
    Pass,
    Function,
    SimilarityMeasure,
)
from linkage.models.client import BaseMPIConnectorClient
from linkage.compare import (
    match_first_four,
    match_exact,
    match_exact_log_odds,
    match_fuzzy_log_odds,
    match_fuzzy,
)

from linkage.block import get_field_value


def link_record(
    patient: Patient, configuration: Configuration, mpi_client: BaseMPIConnectorClient
) -> str | None:
    """
    For each Pass in the configuration do the following:
    1. Fetch blocks of patient data using the provided mpi_client
    2. Compare the provided patient to the fetched blocks using the provided configuration
    3. Add the results to a dictionary: {person_id: str, belongingness_ratio: float}

    Once all Passes have been completed, return the person_id with the highest belongingness_ratio,
    or None if no matches are found
    """
    linkage_scores: dict[str, float] = {}
    for linkage_pass in configuration.passes:
        # Fetch matching patients based on configured blocks
        matching_patients = mpi_client.get_patient_data(patient, linkage_pass.blocks)
        # Create dict of {person_id -> [Patient]} for returned data
        clusters = cluster_patients(matching_patients)

        # For each person in the cluster, do a comparison based on the pass configuration, if score exceeds threshol
        for person_id in clusters:
            # Calculate if the incoming patient record meets the cluster ratio (number of matches / number of returned records for person)
            belongingness_ratio = calculate_belongingness(
                patient, clusters[person_id], linkage_pass
            )
            if (
                belongingness_ratio is not None
                and belongingness_ratio > linkage_pass.args.cluster_ratio
            ):
                linkage_scores[person_id] = belongingness_ratio

    # After all passes, find the strongest match if one exists
    if len(linkage_scores) > 0:
        return max(linkage_scores, key=linkage_scores.get)
    else:
        return None


def cluster_patients(block: list[Patient]) -> dict[str, list[Patient]]:
    """
    Combine a single list of patients into a dictionary of { person_id : list[Patient] }
    """
    clusters: dict[str, list[Patient]] = {}
    for patient in block:
        if patient.person_id is None:
            raise ValueError("Invalid state, encountered patient lacking a person id")
        if patient.person_id not in clusters:
            clusters[patient.person_id] = []
        clusters[patient.person_id].append(patient)

    return clusters


def calculate_belongingness(
    patient: Patient, existing_patients: list[Patient], pass_config: Pass
) -> float:
    """
    Determine how well a patient record fits into a list of existing patient records.
    This is done by applying the supplied Pass configuration and calculating the number
    of matches divided by the total number of existing records.

    If no records match, None is returned
    """
    number_matched = 0
    for existing in existing_patients:
        if apply_pass(patient, existing, pass_config):
            number_matched += 1
    if number_matched == 0:
        return 0
    else:
        return number_matched / len(existing_patients)


def apply_pass(a: Patient, b: Patient, pass_configuration: Pass) -> bool:
    """
    Apply the specified Pass configuration to the provided Patient records.
    Return true if the total score calculated exceeds the configured true match threshold
    """
    total_score = 0.0
    # Run each specified function on the specified field, capturing the score
    for field in pass_configuration.functions:  # dict[Field, Function]
        # Get the actual values to be compared
        a_value = get_field_value(a, field)
        b_value = get_field_value(b, field)
        # Compare the values using the specified function
        total_score += calculate_score(
            a_value,
            b_value,
            pass_configuration.functions[field],
            pass_configuration.args.field_thresholds[field],
            pass_configuration.args.log_odds[field],
            pass_configuration.args.similarity_measure,
        )
    # Check if score for pair meets threshold
    return total_score >= pass_configuration.args.true_match_threshold


def calculate_score(
    a: str,
    b: str,
    function: Function,
    field_threshold: float | None,
    field_log_odds: float | None,
    similarity_measure: SimilarityMeasure | None,
) -> float:
    """
    Compares the two provided strings and calculates a score based on the provided configuration parameters
    """
    match function:
        case Function.FIRST_FOUR:
            return match_first_four(a, b)
        case Function.EXACT_MATCH:
            return match_exact(a, b)
        case Function.LOG_ODDS_EXACT_MATCH:
            return match_exact_log_odds(a, b, field_log_odds)
        case Function.FUZZY_MATCH:
            return match_fuzzy(a, b, similarity_measure, field_threshold)
        case Function.LOG_ODDS_FUZZY_MATCH:
            return match_fuzzy_log_odds(
                a,
                b,
                field_log_odds,
                similarity_measure,
                field_threshold,
            )
    return 0.0
