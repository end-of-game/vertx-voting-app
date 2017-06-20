package fr.treeptik.vertx.worker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.dns.DnsClient;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.ext.web.Router;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class MainWorkerVerticle extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(MainWorkerVerticle.class.getName());

    private SQLConnection connection;
    private RedisClient redis;

    @Override
    public void start() {
        final Router router = Router.router(vertx);

        String redisHost = config().getString("redis.host", "localhost");
        String postgresHost = config().getString("postgres.host", "localhost");

        logger.info("Connect to redis.host: " + redisHost);
        logger.info("Connect to postgres.host: " + postgresHost);

        redis = RedisClient.create(vertx, new RedisOptions().setHost(redisHost).setPort(6379));

        JsonObject postgreSQLClientConfig = new JsonObject().put("host", postgresHost);
        postgreSQLClientConfig.put("database", "postgres")
                .put("username", "postgres");

        AsyncSQLClient postgreSQLClient = PostgreSQLClient.createShared(vertx, postgreSQLClientConfig);
        postgreSQLClient.getConnection(conn -> {
            if (conn.succeeded()) {
                this.connection = conn.result();
                logger.info("Connection POSTGRESQL OK");
                String sql = "CREATE TABLE IF NOT EXISTS votes (id SERIAL PRIMARY KEY," +
                        "vote_id varchar(255), vote varchar(255));";
                this.connection.execute(sql, execute -> {
                    if(execute.succeeded()) {
                        logger.info("Table votes created !");
                    } else {
                        logger.error("Creation table Failed" + execute.cause());
                    }
                });
            } else {
                logger.error("Connection or Operation Failed : " + conn.cause());
                // do not use System.exit...
                vertx.close();
            }
        });

        // Get the votes from Redis
        gatherVotes();
    }


    public void updateVote(String voteId, String vote) {

        String deletePreviousVote = "DELETE FROM votes WHERE vote_id=?";
        JsonArray deleteParams = new JsonArray().add(voteId);
        this.connection.updateWithParams(deletePreviousVote, deleteParams, res -> {
           if(res.succeeded()) {
               UpdateResult updateResult = res.result();
               logger.info("Delete previous vote: " + updateResult.getUpdated());

               String insert = "INSERT INTO votes(vote_id, vote) VALUES(?, ?)";
               JsonArray insertParams = new JsonArray()
                       .add(voteId)
                       .add(vote);
               this.connection.updateWithParams(insert, insertParams, insertRes -> {
                  if(insertRes.succeeded()) {
                      UpdateResult insertResult = insertRes.result();
                      logger.info("Insert vote: " + insertResult.getUpdated());
                  } else {
                      logger.error("Error insert vote " + insertRes.cause());
                      vertx.close();
                  }
               });
           } else {
               logger.error("Error delete previous vote " + res.cause());
               vertx.close();
           }
        });

    }

    public void gatherVotes() {
        vertx.setPeriodic(1000, id -> {
            // This handler will get called every second
            redis.blpop("vote", 2, res -> {
                if (res.succeeded()) {
                    if (res.result()!=null){
                        logger.debug("worker: " + res.result().getString(1));
                        JsonObject voteData = new JsonObject(res.result().getString(1));
                        String voteId = voteData.getString("vote_id");
                        String vote = voteData.getString("vote");
                        this.updateVote(voteId, vote);
                    }
                } else {
                    logger.error("Connection or Operation Failed " + res.cause());
                    vertx.close();
                }
            });

        });
    }

}
