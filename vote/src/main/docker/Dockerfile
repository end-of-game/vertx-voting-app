# Extend vert.x image
FROM vertx/vertx3:3.4.1

ENV VERTICLE_NAME ${main.verticle}
ENV VERTICLE_FILE ${project.artifactId}-fatboyslim.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

LABEL application ${project.build.finalName}
LABEL git.branch ${git.branch}
LABEL git.commit.id ${git.commit.id}
LABEL git.commit.user.email ${git.commit.user.email}
LABEL git.commit.message.full ${git.commit.message.full}
LABEL git.commit.time ${git.commit.time}

# Copy your verticle to the container
COPY $VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]

