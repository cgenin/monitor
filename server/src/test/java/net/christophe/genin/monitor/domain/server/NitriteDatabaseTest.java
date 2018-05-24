package net.christophe.genin.monitor.domain.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.base.NitriteDBManagemementTest;
import org.junit.*;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(VertxUnitRunner.class)
public class NitriteDatabaseTest {


    private static final String PATH_DB = "target/testXDatabaseTest.db";
    private static DeploymentOptions option;
    private final Vertx vertx = Vertx.vertx();


    @BeforeClass
    public static void first() throws Exception {
        option = new NitriteDBManagemementTest(NitriteDatabaseTest.class).deleteAndGetOption();
    }

    @Before
    public void before(TestContext context) {
        vertx.deployVerticle(Database.class.getName(), option, context.asyncAssertSuccess());
    }


    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void should_health_respond_after_starting(TestContext context) {
        Async async = context.async();
        vertx.eventBus().<JsonObject>send(Database.HEALTH, new JsonObject(), msg -> {
            try {
                JsonObject body = msg.result().body();
                context.assertNotNull(body);
                context.assertEquals(false, body.getBoolean("mysql"));
                context.assertNotNull(body.getJsonArray("health"));
                async.complete();
            } catch (Exception ex) {
                context.fail(ex);
            }
        });
    }

    @Test
    public void should_not_create_mysql_schema(TestContext context) {
        Async async = context.async();
        vertx.eventBus().<JsonObject>send(Database.MYSQL_CREATE_SCHEMA, new JsonObject(), msg -> {
            try {
                JsonObject body = msg.result().body();
                context.assertNotNull(body);
                context.assertEquals(false, body.getBoolean("active"));
                context.assertEquals(false, body.getBoolean("creation"));
                async.complete();
            } catch (Exception ex) {
                context.fail(ex);
            }
        });
    }

    @Test
    public void should_not_mysql_on_off(TestContext context) {
        Async async = context.async();

        vertx.eventBus().<JsonObject>send(Database.MYSQL_ON_OFF, new JsonObject(), msg -> {
            context.assertFalse(msg.succeeded());
            async.complete();
        });
    }
}
