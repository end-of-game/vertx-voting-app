package fr.treeptik.vertx.vote;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
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
import java.util.ArrayList;

import static io.netty.handler.codec.rtsp.RtspHeaders.Names.SESSION;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class MainVoteVerticle extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(MainVoteVerticle.class.getName());

    private static final String VOTE_ID = "vote_id";
    private static final String VOTE = "vote";
    private static final String HOSTNAME_FIELD = "hostname";

    private String hostname = "localhost";
    private Integer port;
    private String redisHost;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        try {
            boolean resolve = config().getBoolean("hostname.resolve", false);
            if (resolve) {
                long start = System.currentTimeMillis();
                hostname = InetAddress.getLocalHost().getHostName();
                logger.info("hostname.timex : " + (System.currentTimeMillis()-start));
            }
            logger.info("hostname : " + hostname);
        } catch (Exception e) {
            logger.error(e);
        }
    }

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

        port = config().getInteger("http.port", 8080);
        redisHost = config().getString("redis.host", "localhost");
        logger.info("http.port: "+ port);
        logger.info("redis.host: "+redisHost);

        vertx.createHttpServer()
            .requestHandler(router::accept)
            .listen(port);
    }

    private void info (RoutingContext context) {
        checkID(context);

        JsonObject response = new JsonObject();
        response.put(HOSTNAME_FIELD, hostname);
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

        RedisClient redis = RedisClient.create(vertx, new RedisOptions().setHost(redisHost));
        JsonObject response = new JsonObject();
        response.put(VOTE_ID, checkID(context));
        response.put(VOTE, vote);

        redis.rpush("vote", response.toString(), r -> {
            if (r.succeeded()) {
                logger.info("key stored");
            } else {
                logger.error("Connection or Operation Failed " + r.cause());
            }
        });
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(response.toString());
    }

    private String checkID(RoutingContext context) {
        Cookie cookie = context.getCookie(VOTE);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            SecureRandom random = new SecureRandom();
            String voteId = new BigInteger(130, random).toString(16).substring(0, 16);
            context.addCookie(Cookie.cookie(VOTE, voteId));
            return voteId;
        }
    }
}
