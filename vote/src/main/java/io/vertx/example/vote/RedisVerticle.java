package io.vertx.example.vote;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

/**
 * Created by guillaumeUnice on 24/03/17.
 */
public class RedisVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        // If a config file is set, read the host and port.
        String host = Vertx.currentContext().config().getString("host");
        if (host == null) {
            host = "127.0.0.1";
        }

        // Create the redis client
        final RedisClient client = RedisClient.create(vertx,
                new RedisOptions().setHost(host));
//        client.set("key", "value", r -> {
//            if (r.succeeded()) {
//                System.out.println("key stored");
//                client.get("key", s -> {
//                    System.out.println("Retrieved value: " + s.result());
//                });
//            } else {
//                System.out.println("Connection or Operation Failed " + r.cause());
//            }
//        });
    }
}
