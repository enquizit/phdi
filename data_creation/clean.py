import pandas as pd
import re
from pathlib import Path

from faker import Faker

def remove_trailing_numbers(name):
    """Remove trailing numbers from a given name string."""
    return re.sub(r'\d+$', '', name)

# By default Synthea generates all SSN with the 999 prefix, appends numbers to first and last names, and
# uses a different format for DOB than is expected by DIBBS
def clean_csv(input_csv, output_csv):
    """Read a CSV file, clean the 'FIRST' and 'LAST' columns, and write to a new CSV file."""
    # Initialize Faker
    fake = Faker()

    # Read the CSV file
    df = pd.read_csv(input_csv)
    
    # Clean the 'FIRST' and 'LAST' columns
    df['FIRST'] = df['FIRST'].apply(remove_trailing_numbers)
    df['LAST'] = df['LAST'].apply(remove_trailing_numbers)

    # Replace SSN with fake SSNs
    df['SSN'] = df['SSN'].apply(lambda x: fake.ssn())

    # Add a new column for birthdate
    df['BIRTHDATE'] = df['SSN'].apply(lambda x: fake.date_of_birth().strftime('%Y-%m-%d'))
    
    # Write the cleaned data to a new CSV file
    df.to_csv(output_csv, index=False)
    print(f"Cleaned data has been written to {output_csv}")

# Usage
input_csv = './data/csv/patients.csv'  # Replace with your input CSV file path
Path("./data/cleaned/").mkdir(parents=True, exist_ok=True)
output_csv = './data/cleaned/patients-clean.csv'  # Replace with your desired output CSV file path
clean_csv(input_csv, output_csv)
