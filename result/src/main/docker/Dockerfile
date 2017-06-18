FROM openjdk:8-jre

ENV VERTICLE_FILE ${project.artifactId}-fatboyslim.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles
ENV VERTICLE_CONF $VERTICLE_HOME/application-docker-conf.json

EXPOSE 8081

# Copy your fat jar to the container
COPY $VERTICLE_FILE $VERTICLE_HOME/
COPY application-docker-conf.json $VERTICLE_CONF
COPY log4j2.xml $VERTICLE_HOME

HEALTHCHECK --interval=5s --timeout=3s --retries=3 \
    CMD curl --fail http://localhost:8081/health || exit 1

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.Log4j2LogDelegateFactory \
                -jar $VERTICLE_FILE \
                --conf $VERTICLE_CONF"]

LABEL application ${project.artifactId}
LABEL version ${project.version}
LABEL git.branch ${git.branch}
LABEL git.commit.id.abbrev ${git.commit.id.abbrev}
LABEL git.commit.user.email ${git.commit.user.email}
LABEL git.commit.message.full ${git.commit.message.full}
LABEL git.commit.time ${git.commit.time}



