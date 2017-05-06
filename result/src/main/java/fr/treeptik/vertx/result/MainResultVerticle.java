package fr.treeptik.vertx.result;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainResultVerticle extends AbstractVerticle {

    private Logger logger = LoggerFactory.getLogger(MainResultVerticle.class.getName());

    private String postgresHost;

    private SQLConnection connection;

    @Override
    public void start(Future<Void> future) {
        this.startServer();
        this.startDBLoop();
    }

    public void startDBLoop() {
        postgresHost = config().getString("postgres.port", "localhost");
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
                        this.getVotes();
                    } else {
                        logger.info("Creation table Failed" + execute.cause());
                    }
                });

            } else {
                logger.error("Connection or Operation Failed " + conn.cause());
            }
        });
    }
    public void startServer() {
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

        StaticHandler staticHandler = StaticHandler.create();
        staticHandler.setCachingEnabled(false);

        /* SockJS / EventBus */
        router.route("/eventbus/*").handler(eventBusHandler());
        router.route("/*").handler(staticHandler);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(8081);
    }

    private SockJSHandler eventBusHandler() {
        SockJSHandler handler = SockJSHandler.create(vertx);
        BridgeOptions options = new BridgeOptions();
        PermittedOptions permitted = new PermittedOptions(); /* allow everything, we don't care for the demo */
        options.addOutboundPermitted(permitted);
        handler.bridge(options);
        return handler;
    }

    public void getVotes() {
        vertx.setPeriodic(1000, id -> {
            String query = "SELECT vote, COUNT(id) AS count FROM votes GROUP BY vote";
            this.connection.query(query, queryRes -> {
                if(queryRes.succeeded()) {
                    logger.info( queryRes.result().getResults());
                    vertx.eventBus().publish("result", queryRes.result().getResults().toString());
                } else {
                    logger.info("Error insert vote " + queryRes.cause());
                }
            });
        });
    }

}
