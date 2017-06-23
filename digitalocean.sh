#!/usr/bin/env bash

export TOKEN=$1

# Create manager.
docker-machine create --driver digitalocean --digitalocean-access-token $TOKEN --digitalocean-region lon1 --digitalocean-size 2gb do-manager

# Promote manager.
docker-machine ssh do-manager "docker swarm init --advertise-addr $(docker-machine ip do-manager)"

# Create worker1.
docker-machine create --driver digitalocean --digitalocean-access-token $TOKEN --digitalocean-region lon1 --digitalocean-size 2gb do-worker1
docker-machine create --driver digitalocean --digitalocean-access-token $TOKEN --digitalocean-region lon1 --digitalocean-size 2gb do-worker2

# Promote worker1.
docker-machine ssh do-worker1 "docker swarm join --token `docker $(docker-machine config do-manager) swarm join-token worker -q` $(docker-machine ip do-manager)"

# Promote worker2.
docker-machine ssh do-worker2 "docker swarm join --token `docker $(docker-machine config do-manager) swarm join-token worker -q` $(docker-machine ip do-manager)"

# Copy the file
docker-machine scp ./docker-stack.yml do-manager:/root

# Deploy the stack
docker-machine ssh do-manager "docker stack deploy --compose-file docker-stack.yml do"

# Run Portain
docker-machine ssh do-manager "docker run -d -p 9000:9000 -v /var/run/docker.sock:/var/run/docker.sock portainer/portainer --no-auth"

# docker service update -d --replicas=5 do_vote
# docker service update -d --replicas=0 do_redis

# Promote worker3.
docker-machine create --driver digitalocean --digitalocean-access-token $TOKEN --digitalocean-region lon1 --digitalocean-size 2gb do-worker3

docker-machine ssh do-worker3 "docker swarm join --token `docker $(docker-machine config do-manager) swarm join-token worker -q` $(docker-machine ip do-manager)"

