#backend

##BUILD
mvn clean package

##RUN
java -jar target/maven-verticle-3.4.1-fat.jar
docker run -p 6379:6379 redis:alpine

##DOCKER
###BUILD
docker build -t vertx-voting-app/vote-back:dev .
###RUN
docker run --name vertx-back -t -i -p 8080:8080 -p 6379:6379 vertx-voting-app/vote-back:dev

#frontend

##BUILD
npm i
npm install -g @angular/cli

##RUN
ng serve

##DOCKER
###BUILD
docker build -t vertx-voting-app/vote-front:dev .

###RUN
run --name angular-client -p 4200:4200 vertx-voting-app/vote-front:dev
