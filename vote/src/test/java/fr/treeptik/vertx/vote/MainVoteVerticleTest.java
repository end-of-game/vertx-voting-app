package fr.treeptik.vertx.vote;

import java.nio.Buffer;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class MainVoteVerticleTest {

    private Vertx vertx;

    @Before
    public void prepare() {
        vertx = Vertx.vertx();
    }

    @After
    public void finish(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void start_http_server(TestContext context) {

    }
}

