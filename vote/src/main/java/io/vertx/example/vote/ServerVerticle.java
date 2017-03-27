package io.vertx.example.vote;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.redis.RedisClient;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;

import static io.netty.handler.codec.rtsp.RtspHeaders.Names.SESSION;

/**
 * Created by guillaumeUnice on 27/03/17.
 */
public class ServerVerticle extends AbstractVerticle {

    private static final String VOTE_ID = "vote_id";
    private static final String VOTE = "vote";
    private static final String HOSTNAME = "hostname";

    @Override
    public void start() throws Exception {
        final Router router = Router.router(vertx);

        CorsHandler corsHandler = CorsHandler.create("*");  //Wildcard(*) not allowed if allowCredentials is true
        corsHandler.allowCredentials(true);
        corsHandler.allowedMethod(HttpMethod.OPTIONS);
        corsHandler.allowedMethod(HttpMethod.GET);
        corsHandler.allowedMethod(HttpMethod.POST);
        corsHandler.allowedMethod(HttpMethod.DELETE);
        corsHandler.allowedHeader("Content-Type");

        router.route().handler(corsHandler);
        router.route().handler(CookieHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx, SESSION, 10000)));
        router.route().handler(BodyHandler.create());

        router.route().failureHandler(ErrorHandler.create(true));

        router.post("/")
                .handler(this::vote);

        router.get("/")
                .handler(this::info);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);
    }


    private void info (RoutingContext context) {
        checkID(context);

        String hostname = "";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        JsonObject response = new JsonObject();
        response.put(HOSTNAME, hostname);
        JsonArray choice = new JsonArray();
        choice.add("Cats");
        choice.add("Dogs");
        response.put("choice", choice);

        context.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .end(response.toString());
    }

    private void vote (RoutingContext context) {

        JsonObject requestBody = context.getBodyAsJson();
        final String vote = requestBody.getString(VOTE);

        RedisClient redis = RedisClient.create(Vertx.vertx());
        JsonObject response = new JsonObject();
        response.put(VOTE_ID, checkID(context));
        response.put(VOTE, vote);
        redis.rpush("vote", response.toString(), r -> {
            if (r.succeeded()) {
                System.out.println("key stored");
            } else {
                System.out.println("Connection or Operation Failed " + r.cause());
            }
        });
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(response.toString());
    }

    private String checkID(RoutingContext context) {
        String voteId = context.getCookie(VOTE).getValue();

        if(voteId == null) {
            SecureRandom random = new SecureRandom();
            context.addCookie(Cookie.cookie(VOTE, new BigInteger(130, random).toString(16).substring(0, 16)));
        }
        return voteId;
    }
}
