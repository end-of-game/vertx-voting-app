#!/usr/bin/env bash

export TOKEN=$1

# Create manager.
docker-machine create --driver digitalocean --digitalocean-access-token $TOKEN --digitalocean-region lon1 --digitalocean-size 2gb do-manager

# Promote manager.
docker-machine ssh do-manager "docker swarm init --advertise-addr $(docker-machine ip do-manager)"

# Create worker1.
docker-machine create --driver digitalocean --digitalocean-access-token $TOKEN --digitalocean-region lon1 --digitalocean-size 2gb do-worker1
docker-machine create --driver digitalocean --digitalocean-access-token $TOKEN --digitalocean-region lon1 --digitalocean-size 2gb do-worker2
#docker-machine create --driver digitalocean --digitalocean-access-token $TOKEN --digitalocean-region lon1 --digitalocean-size 2gb do-worker3
#docker-machine create --driver digitalocean --digitalocean-access-token $TOKEN --digitalocean-region lon1 --digitalocean-size 2gb do-worker4

# Promote worker1.
docker-machine ssh do-worker1 "docker swarm join --token `docker $(docker-machine config do-manager) swarm join-token worker -q` $(docker-machine ip do-manager)"

# Promote worker2.
docker-machine ssh do-worker2 "docker swarm join --token `docker $(docker-machine config do-manager) swarm join-token worker -q` $(docker-machine ip do-manager)"

# Copy the file
docker-machine scp ./docker-stack-official.yml do-manager:/root

# Deploy the stack
docker-machine ssh do-manager "docker stack deploy --compose-file docker-stack-official.yml do"

# docker-machine ssh do-manager

# docker stack rm do
# docker service update -d --replicas=5 do_vote
# docker service update -d --replicas=10 do_vote
# docker service update -d --replicas=1 do_vote
# docker service update -d --replicas=10 do_vote

# docker service update --image dockersamples/examplevotingapp_result:after do_result
# docker service update --image dockersamples/examplevotingapp_vote:after do_vote


