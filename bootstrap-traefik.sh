#!/usr/bin/env bash

#!/usr/bin/env bash

export TOKEN=$1

# Create manager.
docker-machine create --driver digitalocean --digitalocean-access-token $TOKEN --digitalocean-region lon1 --digitalocean-size 2gb do-manager

# Promote manager.
docker-machine ssh do-manager "docker swarm init --advertise-addr $(docker-machine ip do-manager)"

docker-machine ssh do-manager "docker network create --driver=overlay --attachable=true traefik-net"
docker-machine ssh do-manager "docker service create \
    --name traefik \
    --constraint=node.role==manager \
    --publish 80:80 --publish 9090:8080 \
    --mount type=bind,source=/var/run/docker.sock,target=/var/run/docker.sock \
    --network traefik-net \
    traefik:1.5 --docker \
    --docker.swarmMode \
    --docker.domain=traefik \
    --docker.watch \
    --api"

# Create worker1.
docker-machine create --driver digitalocean --digitalocean-access-token $TOKEN --digitalocean-region lon1 --digitalocean-size 2gb do-worker1
docker-machine create --driver digitalocean --digitalocean-access-token $TOKEN --digitalocean-region lon1 --digitalocean-size 2gb do-worker2

# Promote worker1.
docker-machine ssh do-worker1 "docker swarm join --token `docker $(docker-machine config do-manager) swarm join-token worker -q` $(docker-machine ip do-manager)"

# Promote worker2.
docker-machine ssh do-worker2 "docker swarm join --token `docker $(docker-machine config do-manager) swarm join-token worker -q` $(docker-machine ip do-manager)"

# Copy the file
docker-machine scp ./docker-stack-traefik.yml do-manager:/root

# Deploy the stack
docker-machine ssh do-manager "docker stack deploy --compose-file docker-stack-traefik.yml do"

