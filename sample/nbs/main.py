import uvicorn
from fastapi import FastAPI
from linkage.link import link_record
from linkage.models.configuration import Configuration
from nbs.mpi.nbs_mpi import NbsMpiClient
from linkage.parse import to_patient
from linkage.models.result import MatchType, Response
from dataclasses import dataclass

app = FastAPI()

## Sample request
# {
#     "patient_resource": {
#         "resourceType": "Patient",
#         "gender": "unknown",
#         "name": [
#             {
#                 "family": "Mouse",
#                 "given": ["Minnie"],
#                 "use": "legal",
#                 "suffix": null
#             }
#         ],
#         "address": [
#             {
#                 "line": ["123 Franklin Pike"],
#                 "city": "Nashville",
#                 "state": "TN",
#                 "postalCode": "37243",
#                 "country": "USA"
#             }
#         ],
#         "telecom": [
#             {
#                 "system": "phone",
#                 "value": "(123) 456 7890",
#                 "use": "work",
#                 "rank": 1
#             }
#         ],
#         "identifier": [
#             {
#                 "system": "http://hospital.com/mrn",
#                 "value": "485-45-4894",
#                 "type": {
#                     "coding": [
#                         {
#                             "system": "http://hl7.org/fhir/v2/0203",
#                             "code": "SS",
#                             "display": "Social Security "
#                         }
#                     ],
#                     "text": "SSN"
#                 },
#                 "assigner": null
#             }
#         ],
#         "birthDate": "1990-01-01"
#     },
#     "configuration": {
#         "passes": [
#             {
#                 "functions": {
#                     "first_name": "log_odds_fuzzy_match",
#                     "last_name": "log_odds_fuzzy_match"
#                 },
#                 "blocks": [
#                     {"field": "birthdate", "transform": null },
#                     {"field": "ssn", "transform": "last_four"},
#                     {"field": "current_sex", "transform": null}

#                 ],
#                 "args": {
#                     "log_odds": {
#                         "first_name": 1.765,
#                         "last_name": 1.2
#                     },
#                     "field_thresholds": {
#                         "first_name": 1,
#                         "last_name" : 1
#                     },
#                     "cluster_ratio": 0.5,
#                     "true_match_threshold": 1.4,
#                     "human_review_threshold": 1,
#                     "similarity_measure": "jarowinkler"
#                 }
#             }
#         ]
#     }
# }

mpi_client = NbsMpiClient()


@dataclass
class SimpleResponse:
    patient: int | None
    match_type: MatchType


@app.post("/match")
async def match(
    patient_resource: dict, configuration: Configuration, log: bool = False
) -> Response | SimpleResponse:
    patient = to_patient(patient_resource)
    if patient is None:
        return Response(None, 0.0, MatchType.NONE)
    response = link_record(patient, configuration, mpi_client)
    if log is True:
        return response
    else:
        return SimpleResponse(response.patient, response.match_type)


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
