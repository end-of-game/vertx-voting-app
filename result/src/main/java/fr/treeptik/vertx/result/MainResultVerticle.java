package fr.treeptik.vertx.result;

import fr.treeptik.vertx.utils.MultipleFutures;
import io.vertx.core.*;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import java.util.ArrayList;
import java.util.List;

public class MainResultVerticle extends AbstractVerticle {

    private List<String> deploymentIds;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        deploymentIds = new ArrayList<>();
    }

    @Override
    public void start(Future<Void> future) {

        MultipleFutures dbDeployments = new MultipleFutures();
        dbDeployments.add(this::deployServer);

        dbDeployments.setHandler(result -> {
            if (result.failed()) {
                future.fail(result.cause());
            } else {
                future.complete();
            }
        });
        dbDeployments.start();
    }
    private void deployServer(Future<Void> future) {
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
