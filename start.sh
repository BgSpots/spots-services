#!/bin/bash

# Stop and remove existing containers
docker-compose down

# Start containers defined in the docker-compose.yml file
docker-compose up -d
