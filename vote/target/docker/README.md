# FRONTWEB

npm serve

# BACKEND

## Devlopment Server
mvn compile exec:java

## DOCKER 

### BUILD
docker build -t vertx-voting-app/vote-back:dev . 

### RUN 
docker run --name vertx-back -t -i -p 8080:8080 vertx-voting-app/vote-back:dev

# test
