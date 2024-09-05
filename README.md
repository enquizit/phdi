## Record Linkage Service

This service is a POC refactoring of the DIBBS Record Linkage service that queries the NBS database directly. The service has also been modified to accept the configuration alongside the FHIR json of the patient data. An optional `log` query parameter can be provided to return extended matching information. 

### Running the docker container
For the container to successfully start and establish a database connection, you must first have a running [nbs-mssql ](https://github.com/CDCgov/NEDSS-Modernization/blob/main/cdc-sandbox/docker-compose.yml) container.

Once the nbs-mssql container is running, start the record linkage service by running the below command.

```bash
docker compose up record-linkage -d
```

Once the container is running a Swagger page is available here:
http://localhost:8000/docs#/

### Making a request
The following json is an example of the data expected by the service. A FHIR json patient resource as well as the configuration are required. 

If the json was saved as a file named `request.json`, then you could POST the request to the record linkage service using the following curl:

```bash
curl "localhost:8000/match?log=True" --data "@request.json" -H "Content-type:Application/json"
```


```json
{
    "patient_resource": {
        "resourceType": "Patient",
        "gender": "male",
        "name": [
            {
                "family": "Washington",
                "given": ["First", "Robert"],
                "use": "legal",
                "suffix": "JR"
            }
        ],
        "address": [
            {
                "line": ["3461 Adams Neck", "Northeast"],
                "city": "Sarahbury",
                "state": "MA",
                "postalCode": "64832",
                "country": "USA"
            }
        ],
        "telecom": [
            {
                "system": "phone",
                "value": "(123) 456 7890",
                "use": "work",
                "rank": 1
            }
        ],
        "identifier": [
            {
                "system": "http://hospital.com/mrn",
                "value": "551-79-0423",
                "type": {
                    "coding": [
                        {
                            "system": "http://hl7.org/fhir/v2/0203",
                            "code": "MR",
                            "display": "Medical Record Number"
                        }
                    ],
                    "text": "MRN"
                },
                "assigner": "Hospital"
            }
        ],
        "birthDate": "2003-10-08"
    },
    "configuration": {
        "passes": [
            {
                "functions": {
                    "first_name": "log_odds_fuzzy_match"
                },
                "blocks": [{ "field": "first_name", "transform": null }],
                "args": {
                    "log_odds": {
                        "first_name": 1.765
                    },
                    "field_thresholds": {
                        "first_name": 1
                    },
                    "cluster_ratio": 0.5,
                    "true_match_threshold": 1.4,
                    "human_review_threshold": 1,
                    "similarity_measure": "jarowinkler"
                }
            }
        ]
    }
}
```


Example response with `?log=True`

```json
{
  "patient": 10055283,
  "match_type": "EXACT",
  "linkage_score": {
    "patient": 10055283,
    "score": 0.5,
    "match_type": "EXACT",
    "cluster_ratio": {
      "exact_match": 0.5,
      "human_review": 0.5,
      "pass_results": [
        {
          "patient": 10055283,
          "field_scores": {
            "first_name": 1.765
          },
          "match_type": "EXACT"
        },
        {
          "patient": 10055285,
          "field_scores": {
            "first_name": 0.0
          },
          "match_type": "NONE"
        }
      ]
    }
  }
}
```