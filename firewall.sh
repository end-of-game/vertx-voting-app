#!/usr/bin/env bash

function apply_firewall() {
    docker-machine ssh $1 "DEBIAN_FRONTEND=noninteractive apt-get install -y iptables-persistent"
    docker-machine ssh $1 "netfilter-persistent flush -y"
    docker-machine ssh $1 "iptables -A INPUT -p tcp --dport 22 -j ACCEPT"
    docker-machine ssh $1 "iptables -A INPUT -p tcp --dport 2376 -j ACCEPT"
    if [[ $2 == "slave" ]] ; then
        docker-machine ssh $1 "iptables -A INPUT -p tcp --dport 2377 -j ACCEPT"
    fi
    docker-machine ssh $1 "iptables -A INPUT -p tcp --dport 7946 -j ACCEPT"
    docker-machine ssh $1 "iptables -A INPUT -p udp --dport 7946 -j ACCEPT"
    docker-machine ssh $1 "iptables -A INPUT -p udp --dport 4789 -j ACCEPT"
    docker-machine ssh $1 "netfilter-persistent save -y"
    docker-machine ssh $1 "systemctl restart docker"
}

# Create manager.
docker-machine create --driver digitalocean --digitalocean-access-token $DIGITALOCEAN_TOKEN --digitalocean-region lon1 --digitalocean-size 8gb fdj-manager

apply_firewall fdj-manager master

# Create worker 1
docker-machine create --driver digitalocean --digitalocean-access-token $DIGITALOCEAN_TOKEN --digitalocean-region lon1 --digitalocean-size 8gb fdj-slave1

apply_firewall fdj-slave1 slave

# Promote manager.
docker-machine ssh fdj-manager "docker swarm init --advertise-addr $(docker-machine ip fdj-manager)"

# Promote worker1.
docker-machine ssh fdj-slave1 "docker swarm join --token `docker $(docker-machine config fdj-manager) swarm join-token worker -q` $(docker-machine ip fdj-manager)"

# Copy the file
docker-machine scp ./docker-stack-official.yml fdj-manager:/root

# Deploy the stack
docker-machine ssh fdj-manager "docker stack deploy --compose-file docker-stack-official.yml fdj"

# docker-machine ssh fdj-manager

# docker stack rm fdj
# docker service update -d --replicas=5 fdj_vote
# docker service update -d --replicas=10 fdj_vote
# docker service update -d --replicas=1 fdj_vote
# docker service update -d --replicas=10 fdj_vote

# docker service update --image dockersamples/examplevotingapp_result:after fdj_result
# docker service update --image dockersamples/examplevotingapp_vote:after fdj_vote
