import json
import os

import requests
from fastapi import HTTPException
from fastapi import Response
from fastapi import WebSocket

from app.handlers import build_fhir_converter_request
from app.handlers import build_geocoding_request
from app.handlers import build_ingestion_dob_request
from app.handlers import build_ingestion_name_request
from app.handlers import build_ingestion_phone_request
from app.handlers import build_message_parser_message_request
from app.handlers import build_message_parser_phdc_request
from app.handlers import build_save_fhir_data_body
from app.handlers import build_validation_request
from app.handlers import ServiceHandlerResponse
from app.handlers import unpack_fhir_converter_response
from app.handlers import unpack_fhir_to_phdc_response
from app.handlers import unpack_ingestion_standardization
from app.handlers import unpack_parsed_message_response
from app.handlers import unpack_save_fhir_data_response
from app.handlers import unpack_validation_response
from app.models import OrchestrationRequest
from app.utils import format_service_url

# Locations of the various services the service will delegate
SERVICE_URLS = {
    "validation": os.environ.get("VALIDATION_URL"),
    "ingestion": os.environ.get("INGESTION_URL"),
    "fhir_converter": os.environ.get("FHIR_CONVERTER_URL"),
    "message_parser": os.environ.get("MESSAGE_PARSER_URL"),
    "save_bundle": os.environ.get("ECR_VIEWER_URL"),
}

# Mappings of endpoint names to the service input and output building
# functions--lets the workflow config drive the API loop with no need
# to change function signatures
ENDPOINT_TO_REQUEST_BODY = {
    "validate": build_validation_request,
    "convert-to-fhir": build_fhir_converter_request,
    "geocode_bundle": build_geocoding_request,
    "standardize_names": build_ingestion_name_request,
    "standardize_dob": build_ingestion_dob_request,
    "standardize_phones": build_ingestion_phone_request,
    "parse_message": build_message_parser_message_request,
    "fhir_to_phdc": build_message_parser_phdc_request,
    "save-fhir-data": build_save_fhir_data_body,
}
ENDPOINT_TO_RESPONSE = {
    "validate": unpack_validation_response,
    "convert-to-fhir": unpack_fhir_converter_response,
    "geocode_bundle": unpack_ingestion_standardization,
    "standardize_names": unpack_ingestion_standardization,
    "standardize_dob": unpack_ingestion_standardization,
    "standardize_phones": unpack_ingestion_standardization,
    "parse_message": unpack_parsed_message_response,
    "fhir_to_phdc": unpack_fhir_to_phdc_response,
    "save-fhir-data": unpack_save_fhir_data_response,
}


def post_request(url: str, payload: dict) -> Response:
    """
    Helper function to post an API request to a particular endpoint using
    the `requests` module.

    :param url: The full URL of the endpoint to-hit.
    :param payload: The body of the Request object, as a dictionary.
    :return: A Response object from the posted endpoint.
    """
    return requests.post(url, json=payload)


async def _send_websocket_dump(
    endpoint_name: str,
    base_response: Response,
    service_response: ServiceHandlerResponse,
    progress_dict: dict,
    websocket: WebSocket,
) -> dict:
    """
    Helper method that sends service response information from a DIBBs
    API call to a particular web socket.

    :param endpoint_name: Name of the endpoint to inform the websocket of.
    :param base_response: The unaltered response the service sent back.
    :param service_response: The unpacked handler response with logic
      applied determining whether the calling function should continue.
    :param progress_dict: The dictionary tracking progres to notify the
      websocket of.
    :param websocket: The websocket to stream the data to.
    :return: The updated progress dictionary.
    """
    status = (
        "success"
        if (service_response.status_code == 200 and service_response.should_continue)
        else "error"
    )

    # Write service responses into websocket message
    progress_dict[endpoint_name] = {
        "status": status,
        "status_code": base_response.status_code,
        "response": base_response.json(),
    }

    await websocket.send_text(json.dumps(progress_dict))
    return progress_dict


async def call_apis(
    config: dict, input: OrchestrationRequest, websocket: WebSocket = None
) -> tuple:
    """
    Asynchronous function that loops over each step in a provided workflow
    config and performs each service step. The function builds a request
    packet for each step, posts to the appropriate API, then unpacks the
    service response. If the response is valid and has data, it is passed
    to the next service as input. If the response was unsuccessful, the
    function communicates errors to the caller.

    :param config: The config-driven workflow extracted from a JSON file.
    :param input: The original request to the orchestration service.
    :param websocket: Optionally, a socket to which to stream input
      bytes on the service's progress and results.
    :return: A tuple holding the concluding status code of the orchestration
      service, as well as each step's response along the way.
    """
    workflow = config.get("workflow", [])
    current_message = input.get("message")
    response = current_message
    responses = {}
    # For websocket json dumps
    progress_dict = {}
    for step in workflow:
        service = step["service"]
        endpoint = step["endpoint"]
        endpoint_name = endpoint.split("/")[-1]
        params = step.get("params", None)

        service_url = format_service_url(SERVICE_URLS[service], endpoint)

        request_body_func = ENDPOINT_TO_REQUEST_BODY[endpoint_name]
        response_func = ENDPOINT_TO_RESPONSE[endpoint_name]
        request_body = request_body_func(current_message, input, params)
        response = post_request(service_url, request_body)
        service_response = response_func(response)

        if websocket:
            progress_dict = await _send_websocket_dump(
                endpoint_name,
                response,
                service_response,
                progress_dict,
                websocket,
            )

        if service_response.status_code != 200:
            raise HTTPException(
                status_code=service_response.status_code,
                detail=f"Service {service} failed with error {service_response.msg_content}",  # noqa
            )

        if not service_response.should_continue:
            raise HTTPException(
                status_code=400,
                detail=f"Service {service} completed, but orchestration cannot continue: "  # noqa
                + f"{service_response.msg_content}",
            )

        # Validation and save_bundle do not contain any updates to the data
        if service not in ["validation", "save_bundle"]:
            current_message = service_response.msg_content
        responses[service] = response
    return (response, responses)
