# Extend vert.x image
FROM vertx/vertx3

ENV VERTICLE_NAME io.vertx.example.vote.WebServer
ENV VERTICLE_FILE target/maven-verticle-3.4.1-fat.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

# Copy your verticle to the container                   (2)
COPY $VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]
