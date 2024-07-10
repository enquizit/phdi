### Test data generation

1. Execute `run_synthea.sh` to run Synthea data generation.
2. Execute `python clean.py` to remove digits from names, format birthdate, and generate a new SSN instead of always having a 999 prefix
3. Execute `python scramble.py` to scramble generated test data
4. Execute `python create-json-bundles.py` to generate a DIBBS FHIR payload for each patient