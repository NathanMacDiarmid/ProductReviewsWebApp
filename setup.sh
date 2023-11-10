#!/bin/bash

# Package project
mvn package

# Build docker image
docker build -t product-reviews-web-app .

# Run docker images with docker-compose
docker compose -f monitoring/docker-compose.yml up -d