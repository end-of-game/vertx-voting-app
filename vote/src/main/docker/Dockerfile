# Extend vert.x image
FROM vertx/vertx3

ENV VERTICLE_NAME ${main.verticle}
ENV VERTICLE_FILE ${project.artifactId}-fatboyslim.jar
#ENV VERTICLE_FILE ${project.artifactId}.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

LABEL application ${project.artifactId}
LABEL version ${project.version}
LABEL git.branch ${git.branch}
LABEL git.commit.id.abbrev ${git.commit.id.abbrev}
LABEL git.commit.user.email ${git.commit.user.email}
LABEL git.commit.message.full ${git.commit.message.full}
LABEL git.commit.time ${git.commit.time}

# Copy your verticle to the container
COPY $VERTICLE_FILE $VERTICLE_HOME/

COPY src/main/resources/logging.properties /usr/local/vertx/conf/logging.properties
COPY webroot $VERTICLE_HOME/webroot

# Launch the verticle
WORKDIR $VERTICLE_HOME

ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]


