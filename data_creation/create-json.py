import json
import os
import csv

# injects csv data into provided json
def create_json(json_data, csv_row):
    entry = json_data['bundle']['entry'][0]
    entry['resource']['name'][0]['family'] = csv_row['LAST']
    entry['resource']['name'][0]['given'][0] = csv_row['FIRST']
    entry['resource']['address'][0]['line'][0] = csv_row['ADDRESS']
    entry['resource']['address'][0]['city'] = csv_row['CITY']
    entry['resource']['address'][0]['state'] = csv_row['STATE']
    entry['resource']['address'][0]['postalCode'] = csv_row['ZIP']
    entry['resource']['identifier'][0]['value'] = csv_row['SSN']
    entry['resource']['birthDate'] = csv_row['BIRTHDATE']
    json_data['external_person_id'] = csv_row['Id']

# Function to save JSON data as text files
def save_json(json_data, folder_name, filename):
    if not os.path.exists(folder_name):
        os.makedirs(folder_name)
    file_path = os.path.join(folder_name, filename)
    with open(file_path, 'w') as file:
        file.write(json.dumps(json_data, indent=4))

# ----------------------- Main function -----------------------------
def main():
    csv_file_path = './data/cleaned/patients-clean.csv'
    json_template_path = './assets/sample.json'
    
    with open(csv_file_path, newline='', encoding='utf-8-sig') as csv_file:
        csv_data = list(csv.DictReader(csv_file))
        
        # Check and print the headers of the CSV
        print("CSV Headers:", csv_data[0].keys())
        
        with open(json_template_path) as f:
            json_data = json.load(f)
        
        for i, csv_row in enumerate(csv_data):
            modified_json_data = json_data.copy()
            create_json(modified_json_data, csv_row)
            folder_name = "./data/bundles"
            filename = f"file_{i}.json"
            save_json(modified_json_data, folder_name, filename)

if __name__ == "__main__":
    main()
