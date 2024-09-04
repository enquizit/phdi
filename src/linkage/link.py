from linkage.models.patient import Patient
from linkage.models.configuration import (
    Configuration,
    Pass,
    Function,
    Field,
    SimilarityMeasure,
)
from linkage.models.result import (
    MatchType,
    ClusterRatio,
    PassResult,
    LinkageScore,
    Response,
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
) -> Response:
    """
    For each Pass in the configuration do the following:
    1. Fetch blocks of patient data using the provided mpi_client
    2. Compare the provided patient to the fetched blocks using the provided configuration

    Once all Passes have been completed, find the linkage score with the highest belongingness/cluster ratio.
    """
    linkage_scores: list[LinkageScore] = []
    for linkage_pass in configuration.passes:
        # Fetch matching patients based on configured blocks
        matching_patients = mpi_client.get_patient_data(patient, linkage_pass.blocks)
        # Create dict of {person_id -> [Patient]} for returned data
        clusters = cluster_patients(matching_patients)

        # For each person in the cluster, determine if it is a match for the cluster
        for person_id in clusters:
            cluster_ratio = calculate_belongingness(
                patient, clusters[person_id], linkage_pass
            )
            if cluster_ratio.exact_match >= linkage_pass.args.cluster_ratio:
                linkage_scores.append(
                    LinkageScore(
                        person_id,
                        cluster_ratio.exact_match,
                        MatchType.EXACT,
                        cluster_ratio,
                    )
                )
            elif (
                linkage_pass.args.human_review_threshold is not None
                and cluster_ratio.human_review
                >= linkage_pass.args.human_review_threshold
            ):
                linkage_scores.append(
                    LinkageScore(
                        person_id,
                        cluster_ratio.human_review,
                        MatchType.HUMAN_REVIEW,
                        cluster_ratio,
                    )
                )

    # After all passes, find the strongest match if one exists
    if len(linkage_scores) == 0:
        return Response(None, MatchType.NONE, None)

    best_match: LinkageScore | None = max(linkage_scores, key=lambda x: x.score)
    if best_match is not None and best_match.score > 0:
        return Response(best_match.patient, best_match.match_type, best_match)
    else:
        return Response(None, MatchType.NONE, None)


def calculate_belongingness(
    patient: Patient, existing_patients: list[Patient], pass_config: Pass
) -> ClusterRatio:
    """
    Determine how well a patient record fits into a list of existing patient records.
    This is done by applying the supplied Pass configuration and calculating the number
    of matches divided by the total number of existing records.

    """
    patient_count = len(existing_patients)
    if patient_count == 0:
        return ClusterRatio(0, 0, [])

    pass_results: list[PassResult] = []
    for existing in existing_patients:
        pass_results.append(apply_pass(patient, existing, pass_config))
    match_types = [p.match_type for p in pass_results]
    exact_count = match_types.count(MatchType.EXACT)
    human_review_count = match_types.count(MatchType.HUMAN_REVIEW)
    exact_ratio = exact_count / patient_count
    human_review_ratio = (exact_count + human_review_count) / patient_count
    return ClusterRatio(exact_ratio, human_review_ratio, pass_results)


def apply_pass(a: Patient, b: Patient, pass_configuration: Pass) -> PassResult:
    """
    Apply the specified Pass configuration to the provided Patient records. This is done by
    calculating the scores by running each function on the specified field from the provided patient.
    Once the scores have been calculated they are summed and compared against the provided thresholds to
    determine the match_type.
    """
    scores: dict[Field, float] = {}
    total_score = 0.0
    # Run each specified function on the specified field, capturing the score
    for field in pass_configuration.functions:  # dict[Field, Function]
        # Get the actual values to be compared
        a_value = get_field_value(a, field)
        b_value = get_field_value(b, field)

        # Compare the values using the specified function
        field_score = calculate_score(
            a_value,
            b_value,
            pass_configuration.functions[field],
            pass_configuration.args.field_thresholds[field],
            pass_configuration.args.log_odds[field],
            pass_configuration.args.similarity_measure,
        )
        total_score += field_score
        scores[field] = field_score
    # Check if score for pair meets threshold
    if total_score >= pass_configuration.args.true_match_threshold:
        return PassResult(b.patient_id, scores, MatchType.EXACT)
    elif (
        pass_configuration.args.human_review_threshold is not None
        and total_score >= pass_configuration.args.human_review_threshold
    ):
        return PassResult(b.patient_id, scores, MatchType.HUMAN_REVIEW)
    else:
        return PassResult(b.patient_id, scores, MatchType.NONE)


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
