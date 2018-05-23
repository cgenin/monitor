package net.christophe.genin.monitor.domain.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.base.NitriteDBManagemementTest;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
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
    private static DeploymentOptions option;
    Vertx vertx;

    @BeforeClass
    public static void first() throws Exception {
        option = new NitriteDBManagemementTest(MysqlDatabaseTest.class).deleteAndGetOption();
    }


    @Before
    public void before(TestContext context) throws IOException {
        vertx = Vertx.vertx();
        vertx.deployVerticle(Console.class.getName(), option, context.asyncAssertSuccess());
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
