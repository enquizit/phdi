import { NextRequest, NextResponse } from "next/server";
import pgPromise from "pg-promise";

/**
 * Handles POST requests and saves the FHIR Bundle to the database.
 *
 * @param request - The incoming request object. Expected to have a JSON body in the format `{"fhirBundle":{}, "saveSource": "postgres|s3""}`.
 * @returns A `NextResponse` object with a JSON payload indicating the success message and the status code set to 200. The response content type is set to `application/json`.
 *
 * @example - Saving to Postgres
 * ```typescript
 * const body = {
    "fhirBundle": {
        "resourceType": "Bundle",
        "type": "batch",
        "entry": [
        {
            "fullUrl": "urn:uuid:12345",
            "resource": {
                "resourceType": "Composition",
                "id": "12345"
            }
        }
        ]
    },
    "saveSource": "postgres"
  }
 * const request = new NextRequest({ body: JSON.stringify(body) });
 * const response = await POST(request);
 * console.log(response);
 * ```
 */

export async function POST(request: NextRequest) {
  let requestBody;
  let fhirBundle;
  let saveSource;
  let ecrId;

  try {
    requestBody = await request.json();
    fhirBundle = requestBody.fhirBundle;
    saveSource = requestBody.saveSource;
    ecrId = requestBody.fhirBundle.entry[0].resource.id;
  } catch (error: any) {
    console.error("Error reading request body:", error);
    return new NextResponse(
      JSON.stringify({ error: "Error reading request body. " + error.message }),
      { status: 400, headers: { "content-type": "application/json" } },
    );
  }

  if (!saveSource) {
    return new NextResponse(
      JSON.stringify({
        message:
          "Save location is undefined. Please provide a valid value for 'saveSource' (postgres or s3).",
      }),
      { status: 400, headers: { "content-type": "application/json" } },
    );
  }

  const db_url = process.env.DATABASE_URL || "";
  const db = pgPromise();
  const database = db(db_url);

  if (fhirBundle && ecrId) {
    const { ParameterizedQuery: PQ } = pgPromise;
    const addFhir = new PQ({
      text: "INSERT INTO fhir VALUES ($1, $2) RETURNING ecr_id",
      values: [ecrId, fhirBundle],
    });

    try {
      const saveECR = await database.one(addFhir);

      return new NextResponse(
        JSON.stringify({
          message: "Success. Saved FHIR Bundle to database: " + saveECR.ecr_id,
        }),
        { status: 200, headers: { "content-type": "application/json" } },
      );
    } catch (error: any) {
      console.error("Error inserting data to database:", error);
      return new NextResponse(
        JSON.stringify({
          message: "Failed to insert data to database." + error.message,
        }),
        { status: 400, headers: { "content-type": "application/json" } },
      );
    }
  } else {
    return new NextResponse(
      JSON.stringify({
        message:
          "Invalid request body. Body must include a FHIR bundle with an ID.",
      }),
      { status: 400, headers: { "content-type": "application/json" } },
    );
  }
}
