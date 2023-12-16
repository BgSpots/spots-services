import json
from pymongo import MongoClient

# MongoDB connection details
mongo_host = "localhost"
mongo_port = 27017

# Connect to MongoDB
client = MongoClient(mongo_host, mongo_port)
db = client["spots"]
spotsCollection= db["spot"]
sequenceCollection= db["database_sequences"]

# Function to insert data from a JSON file into MongoDB
def insert_data_from_json(json_file, collection):
  with open(json_file, "r",encoding="utf-8") as file:
    data = json.load(file)
    inserted_ids = collection.insert_many(data)

  return len(inserted_ids.inserted_ids)

# Insert data from "spots.json"
spots_inserted_count = insert_data_from_json("spots.json", spotsCollection)

# Insert data from "squence.json"
squence_inserted_count = insert_data_from_json("sequence.json", sequenceCollection)

# Print the number of documents inserted from each file
print(f"Inserted {spots_inserted_count} documents from 'spots.json' into MongoDB")
print(f"Inserted {squence_inserted_count} documents from 'squence.json' into MongoDB")
