import uuid
from datetime import date
from unittest.mock import patch

import pytest
from app import utils
from app.main import app
from fastapi.testclient import TestClient

client = TestClient(app)


@pytest.fixture
def fhir_bundle(read_json_from_test_assets):
    return read_json_from_test_assets("demo_phdc_conversion_bundle.json")


expected_successful_response = utils.read_file_from_assets("demo_phdc.xml")


@patch.object(uuid, "uuid4", lambda: "495669c7-96bf-4573-9dd8-59e745e05576")
@patch.object(utils, "get_datetime_now", lambda: date(2010, 12, 15))
def test_endpoint(fhir_bundle):
    test_request = {
        "phdc_report_type": "case_report",
        "message": fhir_bundle,
    }
    actual_response = client.post("/fhir_to_phdc", json=test_request)
    print(actual_response.text)
    assert actual_response.status_code == 200
    assert actual_response.text == expected_successful_response
