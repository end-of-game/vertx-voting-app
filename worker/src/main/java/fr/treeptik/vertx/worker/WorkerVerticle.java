package fr.treeptik.vertx.worker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;


public class WorkerVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) {

//        vertx.setPeriodic(1000, id -> {
//            // This handler will get called every second
//            RedisClient redis = RedisClient.create(vertx, new RedisOptions().setHost("127.0.0.1"));
//            System.out.println("OKKKK stored");
//            redis.get("vote", res -> {
//
//                if (res.succeeded()) {
//                    System.out.println("OKKKK stored");
//                    System.out.println(res.result().toString());
//                } else {
//                    System.out.println("Connection or Operation Failed " + res.cause());
//                }
//            });
//        });
        // If a config file is set, read the host and port.
        String host = Vertx.currentContext().config().getString("host");
        if (host == null) {
            host = "127.0.0.1";
        }

        // Create the redis client
        final RedisClient client = RedisClient.create(vertx,
                new RedisOptions().setHost(host));

        client.set("key", "value", r -> {
            if (r.succeeded()) {
                System.out.println("key stored");
                client.get("key", s -> {
                    System.out.println("Retrieved value: " + s.result());
                });
            } else {
                System.out.println("Connection or Operation Failed " + r.cause());
            }
        });
    }



}
