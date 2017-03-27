package io.vertx.example.vote;


import io.vertx.core.*;
import io.vertx.example.utils.MultipleFutures;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by guillaumeUnice on 24/03/17.
 */
public class WebServer extends AbstractVerticle {

    public static final int REDIS_PORT = 8889;

    private List<String> deploymentIds;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        deploymentIds = new ArrayList<>(3);
    }

    @Override
    public void start(Future<Void> future) {


        MultipleFutures dbDeployments = new MultipleFutures();
        dbDeployments.add(this::deployRedis);
        dbDeployments.add(this::deployServ);

        dbDeployments.setHandler(result -> {
            if (result.failed()) {
                future.fail(result.cause());
            } else {
                future.complete();
            }
        });
        dbDeployments.start();
    }

    private void deployRedis(Future<Void> future) {
        DeploymentOptions options = new DeploymentOptions();
        options.setWorker(true);
        vertx.deployVerticle(RedisVerticle.class.getName(), options, result -> {
            if (result.failed()) {
                future.fail(result.cause());
            } else {
                deploymentIds.add(result.result());
                future.complete();
            }
        });
    }

    private void deployServ(Future<Void> future) {
        DeploymentOptions options = new DeploymentOptions();
        options.setWorker(true);
        vertx.deployVerticle(ServerVerticle.class.getName(), options, result -> {
            if (result.failed()) {
                future.fail(result.cause());
            } else {
                deploymentIds.add(result.result());
                future.complete();
            }
        });
    }

}