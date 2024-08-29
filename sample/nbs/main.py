import uvicorn
from fastapi import FastAPI
from linkage.link import link_record
from linkage.models.patient import Patient, Name
from linkage.models.configuration import (
    Configuration,
    Pass,
    Function,
    Arguments,
    Field,
    SimilarityMeasure,
    BlockCriteria,
)
from mpi.nbs_mpi import NbsMpiClient

app = FastAPI()


patient = Patient(
    None,
    None,
    Name("legal", "Doe", "suffix", ["First", "middle", "second-middle"]),
    None,
    [],
)

blocking_criteria = [BlockCriteria(Field.FIRST_NAME, None)]

config = Configuration(
    [
        Pass(
            {Field.FIRST_NAME: Function.LOG_ODDS_FUZZY_MATCH},
            blocking_criteria,
            Arguments(
                log_odds={Field.FIRST_NAME: 2},
                field_thresholds={Field.FIRST_NAME: 0.7},
                cluster_ratio=0.5,
                true_match_threshold=1,
                human_review_threshold=None,
                similarity_measure=SimilarityMeasure.JAROWINKLER,
            ),
        )
    ]
)

mpi_client = NbsMpiClient()


@app.get("/match")
async def match() -> str:
    result = link_record(patient, config, mpi_client)
    return f"Found result: {result}"


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
