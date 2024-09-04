import uvicorn
from fastapi import FastAPI
from linkage.link import link_record
from linkage.models.configuration import Configuration
from mpi.nbs_mpi import NbsMpiClient
from linkage.parse import to_patient
from linkage.models.result import MatchType, Response
from dataclasses import dataclass

app = FastAPI()

## Sample request
# {
#   "patient_resource": {"resourceType": "Patient",
#     "gender": "male",
#     "name": [
#         {
#             "family": "Washington",
#             "given": ["First", "Robert"],
#             "use": "legal",
#             "suffix": "JR"
#         }
#     ],
#     "address": [
#         {
#             "line": ["3461 Adams Neck", "Northeast"],
#             "city": "Sarahbury",
#             "state": "MA",
#             "postalCode": "64832",
#             "country": "USA"
#         }
#     ],
#     "identifier": [
#         {
#             "system": "http://hospital.com/mrn",
#             "value": "551-79-0423",
#             "type": {
#                 "coding": [
#                     {
#                         "system": "http://hl7.org/fhir/v2/0203",
#                         "code": "MR",
#                         "display": "Medical Record Number"
#                     }
#                 ],
#                 "text": "MRN"
#             },
#             "assigner": "Hospital"
#         }
#     ],
#     "birthDate": "2003-10-08"},
#   "configuration": {
#     "passes": [
#       {
#         "functions": {
#           "first_name": "log_odds_fuzzy_match"
#         },
#         "blocks": [
#           {"field": "first_name", "transform": null}
#         ],
#         "args": {
#           "log_odds": {
#             "first_name": 2
#           },
#           "field_thresholds": {
#             "first_name": 1
#           },
#           "cluster_ratio": 0.5,
#           "true_match_threshold": 1.4,
#           "human_review_threshold": 0,
#           "similarity_measure": "jarowinkler"
#         }
#       }
#     ]
#   }
# }
mpi_client = NbsMpiClient()


@dataclass
class SimpleResponse:
    patient: int | None
    match_type: MatchType


@app.post("/match")
async def match(
    patient_resource: dict, configuration: Configuration, log: bool = True
) -> Response | SimpleResponse:
    patient = to_patient(patient_resource)
    if patient is None:
        return Response(None, 0.0, MatchType.NONE)
    response = link_record(patient, configuration, mpi_client)
    print("log", log)
    if log is True:
        return response
    else:
        return SimpleResponse(response.patient, response.match_type)


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
