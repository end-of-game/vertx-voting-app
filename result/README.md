# Backend

## Development Server
mvn compile exec:java

## DOCKER 

### BUILD
docker build -t vertx-voting-app/result-back:dev . 

### RUN 
docker run --name worker -t -i vertx-voting-app/result-back:dev
