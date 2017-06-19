# Cleaning old manager and worker, you can skip this
```
docker-machine rm -f poc-manager poc-worker1 poc-worker2
```

# Create manager.
```
docker-machine create --driver virtualbox poc-manager
```

# Promote manager.
```
docker-machine ssh poc-manager "docker swarm init --advertise-addr $(docker-machine ip poc-manager)"
```

# Create worker1.
```
docker-machine create --driver virtualbox poc-worker1
docker-machine create --driver virtualbox poc-worker2
```

# Promote worker1.
```
docker-machine ssh poc-worker1 "docker swarm join --token `docker $(docker-machine config poc-manager) swarm join-token worker -q` $(docker-machine ip poc-manager)"
```

# Promote worker2.
```
docker-machine ssh poc-worker2 "docker swarm join --token \
        `docker $(docker-machine config poc-manager) swarm join-token worker -q` \ 
        $(docker-machine ip poc-manager)"
```

# Copy docker-stack.yml to the manager.
```
docker-machine scp ./docker-stack.yml poc-manager:/home/docker/.
```

# Deploy the application stack based on the docker-stack.yml
```
docker-machine ssh poc-manager "docker stack deploy --compose-file docker-stack.yml poc"
```

# Verify machines.
```
docker-machine ls
```

# See things running via browser.

```
open http://$(docker-machine ip poc-manager):5000
open http://$(docker-machine ip pocmanager):8080
open http://$(docker-machine ip pocmanager):8081
```

# Increase verticles
```
docker service update -d --replicas=10 poc_vote
```

# Make a change and build image again and update the image
```
docker service update --image vertxswarm/verticle-result:1.2.0-RELEASE poc_result
```

# Oups we rollback
```
docker service update -d --rollback poc_result
```

# then to play
```
docker-machine create --driver virtualbox poc-worker3
docker-machine ssh poc-worker3 "docker swarm join --token `docker $(docker-machine config poc-manager) swarm join-token worker -q` $(docker-machine ip poc-manager)"

docker-machine create --driver virtualbox poc-worker4
docker-machine ssh poc-worker4 "docker swarm join --token `docker $(docker-machine config poc-manager) swarm join-token worker -q` $(docker-machine ip poc-manager)"
```

