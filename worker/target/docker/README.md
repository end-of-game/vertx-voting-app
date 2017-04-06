# Backend

## Devlopment Server
mvn compile exec:java

## DOCKER 

### BUILD
docker build -t vertx-voting-app/worker:dev . 

### RUN 
docker run --name worker -t -i vertx-voting-app/worker:dev
