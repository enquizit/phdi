from linkage.models.configuration import SimilarityMeasure
import rapidfuzz


def match_first_four(a: str | None, b: str | None) -> float:
    """
    Checks if the first 4 characters of both provided strings are equal. If both are None, they are considered equal

    Returns 1 if equal, 0 if not equal.
    """
    a_first_four = a[: min(4, len(a))] if a is not None else None
    b_first_four = b[: min(4, len(b))] if b is not None else None
    return 1.0 if a_first_four == b_first_four else 0.0


def match_exact(a: str | None, b: str | None) -> float:
    """
    Checks if the provided strings are equal. If both are None, they are considered equal

    Returns 1 if equal, 0 if not equal.
    """
    return 1.0 if a == b else 0.0


def match_exact_log_odds(a: str | None, b: str | None, log_odds: float | None) -> float:
    """
    Checks if the provided strings are equal. If both are None, they are considered equal

    Returns provided log_odds value if equal, 0 if not equal.
    """
    if log_odds is None:
        raise ValueError("Missing required log odds")
    return log_odds if a == b else 0.0


def match_fuzzy(
    a: str | None,
    b: str | None,
    similarity_measure: SimilarityMeasure,
    threshold: float | None,
) -> float:
    """
    Checks if the provided strings exceed the threshold using the provided similarity measure.
    If both are None, they are considered a match

    Returns 1 if strings exceed the threshold, otherwise 0.
    """
    if threshold is None or threshold > 1 or threshold < 0:
        raise ValueError("Threshold must be a value between 0 and 1.")

    # Allow None and None to match
    if a is None and b is None:
        return 1
    score = apply_similarity_measure(a, b, similarity_measure)
    return 1 if score >= threshold else 0.0


def match_fuzzy_log_odds(
    a: str | None,
    b: str | None,
    log_odds: float | None,
    similarity_measure: SimilarityMeasure,
    threshold: float | None,
) -> float:
    """
    Checks if the provided strings exceed the threshold using the provided similarity measure.
    If both are None, they are considered a match

    Returns log_odds * score if strings exceed the threshold, otherwise 0.
    """
    if log_odds is None:
        raise ValueError("Missing required log odds")
    if threshold is None or threshold > 1 or threshold < 0:
        raise ValueError("Threshold must be a value between 0 and 1.")

    # Allow None and None to match
    if a is None and b is None:
        return log_odds
    score = apply_similarity_measure(a, b, similarity_measure)
    return log_odds * score if score >= threshold else 0.0


def apply_similarity_measure(
    a: str, b: str, similarity_measure: SimilarityMeasure
) -> float:
    match similarity_measure:
        case SimilarityMeasure.JAROWINKLER:
            return rapidfuzz.distance.JaroWinkler.normalized_similarity(a, b)
        case _:
            raise ValueError("Unsupported SimilarityMeasure specified")
