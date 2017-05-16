# Cleaning old manager and worker, you can skip this
docker-machine rm -f poc-manager && docker-machine rm -f poc-worker1

# Create manager.
docker-machine create --driver virtualbox poc-manager

# Promote manager.
docker-machine ssh poc-manager "docker swarm init --advertise-addr $(docker-machine ip poc-manager)"

# Create worker1.
docker-machine create --driver virtualbox poc-worker1
docker-machine create --driver virtualbox poc-worker2

# Promote worker1.
docker-machine ssh poc-worker1 "docker swarm join --token `docker $(docker-machine config manager) swarm join-token worker -q` $(docker-machine ip manager)"

# Promote worker2.
docker-machine ssh poc-worker2 "docker swarm join --token `docker $(docker-machine config manager) swarm join-token worker -q` $(docker-machine ip manager)"

# Copy docker-stack.yml to the manager.
docker-machine scp ./docker-stack.yml manager:/home/docker/.

# Deploy the application stack based on the `docker-stack.yml`.
docker-machine ssh manager "docker stack deploy --compose-file docker-stack.yml poc"

# Verify machines.
docker-machine ls

# See things running via browser.
open http://$(docker-machine ip manager):5000
open http://$(docker-machine ip manager):5001
open http://$(docker-machine ip manager):8080

# Make a change and build image again
# Update the image
docker service update --image zepouet/examplevotingapp_vote:v2 poc_vote
# Oups erreur on retourne en arrière
docker service update --rollback vote_vote

docker-machine create --driver virtualbox worker3
docker-machine ssh worker3 "docker swarm join --token `docker $(docker-machine config manager) swarm join-token worker -q` $(docker-machine ip manager)"

docker-machine create --driver virtualbox worker4
docker-machine ssh worker4 "docker swarm join --token `docker $(docker-machine config manager) swarm join-token worker -q` $(docker-machine ip manager)"

docker service update --replicas=0 vote_result
