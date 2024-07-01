import os
import io
import pathlib
import json
from math import log
from random import seed
from typing import Union
from random import sample
from typing import List
from itertools import combinations

import pandas as pd

def generate_log_odds():
    csv = """BIRTHDATE,FIRST,LAST,ADDRESS,CITY,STATE,ZIP,SSN,ID
2003-10-08,Robert,Washington,3461 Adams Neck,Sarahbury,MA,64832,551-79-0423,0
2003-10-08,Rob,Washington,3461 Adams Neck,Sarahbury,MA,64832,551-79-0423,1
1982-08-18,Angel,Wilson,733 Wilson Stream Apt. 364,Victoriachester,MA,64832,725-38-5681,2
    """
    data = pd.read_csv(io.StringIO(csv), index_col=False, dtype="object", keep_default_na=False)

    true_matches = {
        0: {1}
    }

    m_probs = calculate_m_probs(data, true_matches, file_to_write="log_odds/m_probs")
    print(m_probs)
    u_probs = calculate_u_probs(
        data, true_matches, file_to_write="log_odds/u_probs"
    )
    calculate_log_odds(m_probs, u_probs, file_to_write="log_odds/log_odds")


def calculate_log_odds(
    m_probs: dict,
    u_probs: dict,
    file_to_write: Union[pathlib.Path, None] = None,
):
    """
    Calculate the per-field log odds ratio score that two records will
    match in a given field. Measures the likelihood that two records
    match on a column due to being a true match as opposed to random
    chance.

    :param m_probs: A dictionary of m-probabilities computed per field.
    :param u_probs: A dictionary of u_probabilities computed per field.
    :param file_to_write: Optionally, a destination filepath at which
      to write the probabilities in JSON format. Default is None.
    :raises ValueError: If the supplied m- and u- probability dictionaries
      do not share an equal key set.
    """
    if m_probs.keys() != u_probs.keys():
        raise ValueError(
            "m- and u- probability dictionaries must contain the same set of keys"
        )
    log_odds = {}
    for k in m_probs:
        log_odds[k] = log(m_probs[k]) - log(u_probs[k])
    _write_prob_file(log_odds, file_to_write)
    return log_odds


def calculate_m_probs(
    data: pd.DataFrame,
    true_matches: dict,
    cols: Union[List[str], None] = None,
    file_to_write: Union[pathlib.Path, None] = None,
):
    """
    For a given set of patient records, calculate the per-field
    m-probability. The m-probability for field X is defined as the
    probability that a pair of records A and B have the same value in
    X, given that A and B are a true matching pair. This function
    incorporates LaPlacian Smoothing to account for unseen data and
    to resolve future logarithms against 0.

    :param data: A pandas dataframe of patient records to compute
      probabilities for.
    :param true_matches: A dictionary holding the IDs of record pairs
      that are true matches in the data set. The format of the dictionary
      should be such that the IDs of the "lower numbered" records in each
      match pair are the keys, and the values are sets of the "higher
      numbered" records in each pair.
    :param cols: Optionally, a list of columns to compute probabilities
      for. If not supplied, computes probabilities across all fields.
      Default is None.
    :param file_to_write: Optionally, a destination filepath at which to
      write the probabilities in JSON format. Default is None.
    """
    if cols is None:
        cols = data.columns
    m_probs = {c: 1.0 for c in cols}
    total_pairs = 1.0
    for root_record, paired_records in true_matches.items():
        total_pairs += len(paired_records)
        for pr in paired_records:
            print(pr)
            for c in cols:
                if data[c].iloc[root_record] == data[c].iloc[pr]:
                    m_probs[c] += 1
    for c in cols:
        m_probs[c] /= total_pairs
    _write_prob_file(m_probs, file_to_write)
    return m_probs


def calculate_u_probs(
    data: pd.DataFrame,
    true_matches: dict,
    n_samples: Union[int, None] = None,
    cols: Union[List, None] = None,
    file_to_write: Union[pathlib.Path, None] = None,
):
    """
    For a given set of patient records, calculate the per-field
    u-probability. The u-probability for field X is defined as the
    probability that a pair of records A and B have the same value in
    X, given that A and B are not a true matching pair. This function
    incorporates LaPlacian Smoothing to account for unseen data and
    to handle future logarithms against 0.

    Note: This function can be slow to compute for large data sets.
    It is recommended to pass only a representative subsample of the
    data to the function (we recommend sampling ~25k candidate pairs
    from a sub-sample of ~25k records), even if the sample operation
    is used.

    :param data: A pandas dataframe of patient records to compute
      probabilities for.
    :param true_matches: A dictionary holding the IDs of record pairs
      that are true matches in the data set. The format of the dictionary
      should be such that the IDs of the "lower numbered" records in each
      match pair are the keys, and the values are sets of the "higher
      numbered" records in each pair.
    :param n_samples: Optionally, a number of samples to take from the
      list of possible pairs to compute probabilities over.
    :param cols: Optionally, a list of columns to compute probabilities
      for. If not supplied, computes probabilities across all fields.
      Default is None.
    :param file_to_write: Optionally, a destination filepath at which to
      write the probabilities in JSON format. Default is None.
    """
    if cols is None:
        cols = data.columns

    u_probs = {c: 1.0 for c in cols}

    # Want only the pairs of candidates that aren't true matches
    base_pairs = list(combinations(data.index, 2))
    neg_pairs = [
        x
        for x in base_pairs
        if x[0] not in true_matches or x[1] not in true_matches[x[0]]
    ]

    if n_samples is not None and n_samples < len(neg_pairs):
        neg_pairs = sample(neg_pairs, n_samples)
    for index in neg_pairs:
        for c in cols:
            if data[c].iloc[index[0]] == data[c].iloc[index[1]]:
                u_probs[c] += 1.0

    for c in cols:
        if n_samples is not None and n_samples < len(neg_pairs):
            u_probs[c] = u_probs[c] / (n_samples + 1.0)
        else:
            u_probs[c] = u_probs[c] / (len(neg_pairs) + 1.0)

    _write_prob_file(u_probs, file_to_write)
    return u_probs

def _write_prob_file(prob_dict: dict, file_to_write: Union[pathlib.Path, None]):
    """
    Helper method to write a probability dictionary to a JSON file, if
    a valid path is supplied.

    :param prob_dict: A dictionary mapping column names to the log-probability
      values computed for those columns.
    :param file_to_write: Optionally, a path variable indicating where to
      write the probabilities in a JSON format. Default is None (meaning this
      function would execute nothing.)
    """
    if file_to_write is not None:
        file_path = os.path.dirname(os.path.realpath(__file__)) + "/" + file_to_write
        os.makedirs(os.path.dirname(file_path), exist_ok=True)
        with open(file_path, "w") as out:
            out.write(json.dumps(prob_dict))


def load_json_probs(path: pathlib.Path):
    """
    Load a dictionary of probabilities from a JSON-formatted file.
    The probabilities correspond to previously computed m-, u-, or
    log-odds probabilities derived from patient records, with one
    score for each field (column) appearing in the data.

    :param path: The file path to load the data from.
    :return: A dictionary of probability scores, one for each field
      in the data set on which they were computed.
    :raises FileNotFoundError: If a file does not exist at the given
      path.
    :raises JSONDecodeError: If the file cannot be read as valid JSON.
    """
    try:
        with open(path, "r") as file:
            prob_dict = json.load(file)
        return prob_dict
    except FileNotFoundError:
        raise FileNotFoundError(f"The specified file does not exist at {path}.")
    except json.decoder.JSONDecodeError as e:
        raise json.decoder.JSONDecodeError(
            "The specified file is not valid JSON.", e.doc, e.pos
        )


generate_log_odds()