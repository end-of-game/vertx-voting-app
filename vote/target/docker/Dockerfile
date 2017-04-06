# Extend vert.x image
FROM vertx/vertx3:3.4.1

ENV VERTICLE_NAME fr.treeptik.vertx.vote.MainVoteVerticle
ENV VERTICLE_FILE verticle-vote-fatboyslim.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

LABEL application verticle-vote-1.0-SNAPSHOT
LABEL git.branch master
LABEL git.commit.id 8fcfcfd4dd3e6dc0ae6fadc14b110b4183eaeee2
LABEL git.commit.user.email zepouet@gmail.com
LABEL git.commit.message.full Add Maven Spotify plugin
LABEL git.commit.time 20170406-100721

# Copy your verticle to the container
COPY $VERTICLE_FILE $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]

