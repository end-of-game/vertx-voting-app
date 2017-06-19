#!/usr/bin/env bash

# Cleaning old manager and worker, you can skip this
docker-machine rm -f poc-manager poc-worker1 poc-worker2

# Create manager.
docker-machine create --driver virtualbox poc-manager

# Promote manager.
docker-machine ssh poc-manager "docker swarm init --advertise-addr $(docker-machine ip poc-manager)"

# Create worker1.
docker-machine create --driver virtualbox poc-worker1
docker-machine create --driver virtualbox poc-worker2

# Promote worker1.
docker-machine ssh poc-worker1 "docker swarm join --token `docker $(docker-machine config poc-manager) swarm join-token worker -q` $(docker-machine ip poc-manager)"

# Promote worker2.
docker-machine ssh poc-worker2 "docker swarm join --token `docker $(docker-machine config poc-manager) swarm join-token worker -q` $(docker-machine ip poc-manager)"
