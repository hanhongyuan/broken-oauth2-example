#!/usr/bin/env bash

set -e

# Build the project and docker images
mvn -T 1C clean install -DskipTests

# Export the active docker machine IP
export DOCKER_IP=$(docker-machine ip)

# docker-machine doesn't exist in Linux, assign default ip if it's not set
DOCKER_IP=${DOCKER_IP:-0.0.0.0}

# Make some space by removing dangling images
echo "Removed $(docker rmi --force $(docker images -qf dangling=true) | grep Deleted | wc -l) dangling images."

# Remove existing containers
docker-compose stop
docker-compose rm -f

# Start all containers
docker-compose up -d --remove-orphans

# Attach to the log output of the cluster
docker-compose logs -f
