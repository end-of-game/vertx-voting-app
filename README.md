# RUN DATABASES

docker run -d -p 6379:6379 --name vds-redis redis:alpine

docker run --name vds-postgres -p 5432:5432 -e POSTGRES_USER=postgres -d postgres

Example Voting App
=========

Getting started
---------------

Download [Docker](https://www.docker.com/products/overview). If you are on Mac or Windows, [Docker Compose](https://docs.docker.com/compose) will be automatically installed. On Linux, make sure you have the latest version of [Compose](https://docs.docker.com/compose/install/).

Run in this directory:
```
docker-compose up
```
The app will be running at [http://localhost:8000](http://localhost:8000), and the results will be at [http://localhost:8081](http://localhost:8081).

Alternately, if you want to run it on a [Docker Swarm](https://docs.docker.com/engine/swarm/), first make sure you have a swarm. If you don't, run:
```
docker swarm init
```
Once you have your swarm, in this directory run:
```
docker stack deploy --compose-file docker-stack.yml vote
```

Architecture
-----

![Architecture diagram](architecture.png)

* A verticle webapp which lets you vote between two options
* A Redis queue which collects new votes
* A verticle worker which consumes votes and stores them in…
* A Postgres database backed by a Docker volume
* A verticle webapp which shows the results of the voting in real time

Note
----

The voting application only accepts one vote per client. It does not register votes if a vote has already been submitted from a client.

