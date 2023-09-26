import json
from pymongo import MongoClient

# MongoDB connection details
mongo_host = "localhost"  # Replace with your MongoDB server host
mongo_port = 27017        # Replace with your MongoDB server port

# Connect to MongoDB
client = MongoClient(mongo_host, mongo_port)
db = client["spots"]  # Replace with your database name
collection = db["spots"]  # Replace with your collection name

# Read data from JSON file
with open("data.json", "r") as file:
  data = json.load(file)

# Insert data into MongoDB
inserted_ids = collection.insert_many(data)

print(f"Inserted {len(inserted_ids.inserted_ids)} documents into MongoDB")
