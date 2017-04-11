# Extend vert.x image
FROM vertx/vertx3:3.4.1

ENV VERTICLE_NAME fr.treeptik.vertx.worker.MainWorkerVerticle
ENV VERTICLE_FILE verticle-worker-fatboyslim.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

LABEL application verticle-worker-1.0-SNAPSHOT
LABEL git.branch master
LABEL git.commit.id.abbrev 3101acb
LABEL git.commit.user.email guillaume.unice@gmail.com
LABEL git.commit.message.full Update favicon + build maven cp webroot directory
LABEL git.commit.time 20170411-120106

# Copy your verticle to the container
COPY $VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]

