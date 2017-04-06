# Extend vert.x image
FROM vertx/vertx3:3.4.1

ENV VERTICLE_NAME fr.treeptik.vertx.vote.MainVoteVerticle
ENV VERTICLE_FILE verticle-vote-fatboyslim.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

LABEL application verticle-vote-1.0-SNAPSHOT
LABEL git.branch master
LABEL git.commit.id 21bf8b846501ab8a50f4c046d79aa61eedc25487
LABEL git.commit.user.email zepouet@gmail.com
LABEL git.commit.message.full Update
LABEL git.commit.time 20170405-190613

# Copy your verticle to the container
COPY $VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]

