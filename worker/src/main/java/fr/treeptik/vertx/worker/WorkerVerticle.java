package fr.treeptik.vertx.worker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class WorkerVerticle extends AbstractVerticle {
    private SQLConnection connection;
    private  RedisClient redis;

    @Override
    public void start(Future<Void> future) {
        redis = RedisClient.create(vertx, new RedisOptions().setHost("127.0.0.1"));

        JsonObject postgreSQLClientConfig = new JsonObject().put("host", "localhost");
        postgreSQLClientConfig.put("database", "postgres")
                .put("username", "postgres");

        AsyncSQLClient postgreSQLClient = PostgreSQLClient.createShared(vertx, postgreSQLClientConfig);


        postgreSQLClient.getConnection(conn -> {
            if (conn.succeeded()) {
                this.connection = conn.result();
                System.out.println("Connection POSTGRESQL OK");
                String sql = "CREATE TABLE IF NOT EXISTS votes (id SERIAL PRIMARY KEY," +
                        "vote_id varchar(255), vote varchar(255));";

                this.connection.execute(sql, execute -> {
                    if(execute.succeeded()) {
                        System.out.println("Table votes created !");
                        this.getVotes();
                    } else {
                        System.out.println("Creation table Failed" + execute.cause());
                    }
                });

            } else {
                System.out.println("Connection or Operation Failed " + conn.cause());
            }
        });
//        final JDBCClient jdbc = JDBCClient.createNonShared(vertx, new JsonObject()
//                .put("url", "jdbc:postgresql://127.0.0.1:5432/postgres")
//                .put("user", "postgres")
////                .put("password", "abc2015")
//        );
//        System.out.println("Connection or Operation Failed ");
//        jdbc.getConnection(res -> {
//
//            if (res.succeeded()) {
//                SQLConnection connection = res.result();
//
//                System.out.println("RESULT null");
//            } else {
//                System.out.println("Connection or Operation Failed " + res.cause());
//            }
//        });
    }

    public void updateVote(String voteId, String vote) {

//        JsonObject postgreSQLClientConfig = new JsonObject().put("host", "127.0.0.1");
//        AsyncSQLClient postgreSQLClient = PostgreSQLClient.createShared(vertx, postgreSQLClientConfig);
//
//        postgreSQLClient.getConnection(res -> {
//            if (res.succeeded()) {
//                SQLConnection connection = res.result();
//
//                System.out.println("RESULT null");
//            } else {
//                System.out.println("Connection or Operation Failed " + res.cause());
//            }
//        });

        String deletePreviousVote = "DELETE FROM votes WHERE vote_id=?";
        JsonArray deleteParams = new JsonArray().add(voteId);
        this.connection.updateWithParams(deletePreviousVote, deleteParams, res -> {
           if(res.succeeded()) {
               UpdateResult updateResult = res.result();
               System.out.println("Delete previous vote: " + updateResult.getUpdated());

               String insert = "INSERT INTO votes(vote_id, vote) VALUES(?, ?)";
               JsonArray insertParams = new JsonArray()
                       .add(voteId)
                       .add(vote);
               this.connection.updateWithParams(insert, insertParams, insertRes -> {
                  if(insertRes.succeeded()) {
                      UpdateResult insertResult = insertRes.result();
                      System.out.println("Insert vote: " + insertResult.getUpdated());
                  } else {
                      System.out.println("Error insert vote " + insertRes.cause());
                  }
               });
           } else {
               System.out.println("Error delete previous vote " + res.cause());
           }
        });

    }

    public void getVotes() {
        vertx.setPeriodic(1000, id -> {
            // This handler will get called every second
            redis.blpop("vote", 2, res -> {
                if (res.succeeded()) {
                    if (res.result()!=null){
                        System.out.println(res.result().getString(1));
                        JsonObject voteData = new JsonObject(res.result().getString(1));
                        String voteId = voteData.getString("vote_id");
                        String vote = voteData.getString("vote");
                        this.updateVote(voteId, vote);
                    }
//                    else {
//                        System.out.println("RESULT null");
//                    }
                } else {
                    System.out.println("Connection or Operation Failed " + res.cause());
                }
            });

        });
    }

}
