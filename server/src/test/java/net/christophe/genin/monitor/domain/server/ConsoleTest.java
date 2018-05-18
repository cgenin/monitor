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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

@RunWith(VertxUnitRunner.class)
public class ConsoleTest {

    public static final String PATH_DB = "target/ConsoleTest.db";
    Vertx vertx;


    @Before
    public void before(TestContext context) throws IOException {
        System.out.println("deleted " + Files.deleteIfExists(Paths.get(new File(PATH_DB).toURI())));

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("nitritedb", new JsonObject().put("path", PATH_DB)));

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
