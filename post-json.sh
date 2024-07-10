#!/bin/bash

# Replace file_directory with new folder for every new run
file_directory="./data_creation/data/bundles"  

post_message_url="http://localhost:8081/link-record"
header1="Content-Type: application/json"
header2="bundle: [object Object]"
header3="use_enhanced: true"
header4="algo_config: [object Object]" 
header5="external_person_id: string"

# Confirming Directory
echo "Files in directory:"
ls "$file_directory"

# Start timing
start_time=$(date +%s)

# Create a result folder
RESULT_FOLDER="${file_directory}/results"
mkdir -p "$RESULT_FOLDER"

for filename in "$file_directory"/*.json; do
    if [ -f "$filename" ]; then
        message_content=$(cat "$filename")
        echo "Sending request for file: $filename"
        # Uncomment the next line if you want to print the content
        #echo "Content: $message_content"

        response=$(curl -X POST \
            -H "$header1" \
            -H "$header2" \
            -H "$header3" \
            -H "$header4" \
            -H "$header5" \
            -d "$message_content" \
            -w "\nHTTP Response Code: %{http_code}\n" \
            -v \
            -s \
            "$post_message_url")

        echo "Response for file: $filename"
        echo "$response"

        # Save response to a file in the result folder
        response_filename="${RESULT_FOLDER}/response_$(basename "$filename" .json).txt"
        echo "$response" > "$response_filename"
    fi
done

# End timing
end_time=$(date +%s)
execution_time=$((end_time - start_time))
echo "Total execution time: $execution_time seconds"