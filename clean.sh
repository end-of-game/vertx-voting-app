#!/usr/bin/env bash

docker-compose kill
docker-compose rm -f
docker container prune
docker volume prune