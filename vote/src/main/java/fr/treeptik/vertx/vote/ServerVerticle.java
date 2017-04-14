package fr.treeptik.vertx.vote;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;

import static io.netty.handler.codec.rtsp.RtspHeaders.Names.SESSION;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class ServerVerticle extends AbstractVerticle {

    private static final String VOTE_ID = "vote_id";
    private static final String VOTE = "vote";
    private static final String HOSTNAME = "hostname";

    JsonObject config = new JsonObject().put("keyStore", new JsonObject()
            .put("path", "keystore.jceks")
            .put("type", "jceks")
            .put("password", "secret"));

    JWTAuth provider = null;
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
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        router.route().handler(BodyHandler.create());
        
        router.route().failureHandler(ErrorHandler.create(true));

        router.get("/vote").handler(this::info);
        router.post("/vote").handler(this::vote);

        StaticHandler staticHandler = StaticHandler.create();
        staticHandler.setCachingEnabled(false);
        router.route("/*").handler(StaticHandler.create());
        this.provider = JWTAuth.create(vertx, this.config);

        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8080);
    }

    private void info (RoutingContext context) {
        checkID(context).setHandler(result -> {
            JsonObject response = new JsonObject();
            response.put(HOSTNAME, "zepouetstation");
            JsonArray choice = new JsonArray();
            choice.add("Cats");
            choice.add("Dogs");
            response.put("choice", choice);
            context.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .end(response.toString());
        });
    }

    private void vote (RoutingContext context) {
        JsonObject requestBody = context.getBodyAsJson();
        final String vote = requestBody.getString(VOTE);
        RedisClient redis = RedisClient.create(vertx, new RedisOptions().setHost("vds-redis"));
        JsonObject response = new JsonObject();
        checkID(context).setHandler(result -> {
            if(result.failed()) {
                result.cause();
            }
            response.put(VOTE_ID, result.result().toString());
            response.put(VOTE, vote);
            redis.rpush("vote", response.toString(), r -> {
                if (r.succeeded()) {
                    System.out.println("key stored");
                } else {
                    System.out.println("Connection or Operation Failed " + r.cause());
                }
            });
            context.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(response.toString());
        });
    }

    private Future<String> checkID(RoutingContext context) {
        Future<String> future = Future.future();
        Cookie cookie = context.getCookie(VOTE);

        if (cookie != null) {
            JsonObject authInfo = new JsonObject().put("jwt", cookie.getValue());
            this.provider.authenticate(authInfo, res -> {
                if (res.failed()) {
                    future.fail(res.cause());
                }
                future.complete((String) res.result().principal().getValue("sub"));
            });
        } else {
            SecureRandom random = new SecureRandom();
            String voteId = new BigInteger(130, random).toString(16).substring(0, 16);
            String token = this.provider.generateToken(new JsonObject().put("sub", voteId), new JWTOptions().setAlgorithm("RS256").setExpiresInMinutes(60L));
            context.addCookie(Cookie.cookie(VOTE, token));
            future.complete(voteId);
        }
        return future;
    }
}
