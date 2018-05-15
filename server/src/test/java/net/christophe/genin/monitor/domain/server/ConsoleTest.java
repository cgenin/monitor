package net.christophe.genin.monitor.domain.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

@RunWith(VertxUnitRunner.class)
public class ConsoleTest {

    Vertx vertx;


    @Before
    public void before(TestContext context) {
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("nitritedb", new JsonObject().put("path", "target/testInitializdb.db")));

        vertx = Vertx.vertx();
        vertx.deployVerticle(Console.class.getName(), options, context.asyncAssertSuccess());
    }


    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void should_create_message(TestContext context) {
        Async async = context.async();
        long time = new Date().getTime();
        vertx.eventBus().<JsonObject>consumer(Console.CONSOLE, msg -> {
            JsonObject body = msg.body();
            context.assertEquals("Ceci est un test", body.getString("msg"));
            context.assertTrue(body.getLong("date") > time);
            context.assertEquals("info", body.getString("type"));
            async.complete();
        });
        vertx.eventBus().send(Console.INFO, "Ceci est un test");
    }
}
