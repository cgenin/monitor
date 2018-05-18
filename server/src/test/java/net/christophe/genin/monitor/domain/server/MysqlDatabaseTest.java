package net.christophe.genin.monitor.domain.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.adapter.nitrite.NitriteConfiguration;
import net.christophe.genin.monitor.domain.server.model.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(VertxUnitRunner.class)
public class MysqlDatabaseTest extends DbTest {


    public static final String PATH_DB = "target/testMysqlDatabaseTest.db";
    Vertx vertx;


    @Before
    public void before(TestContext context) throws IOException {
        Files.deleteIfExists(Paths.get(new File(PATH_DB).toURI()));
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("nitritedb", new JsonObject().put("path", PATH_DB)));

        vertx = Vertx.vertx();
        Async async = context.async(3);
        vertx.deployVerticle(Database.class.getName(), options, r -> {
            async.countDown();
            setAntiMonitorDS(context, async, vertx);

        });

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
                context.assertEquals(true, body.getBoolean("mysql"));
                context.assertNotNull(body.getJsonArray("health"));
                async.complete();
            } catch (Exception ex) {
                context.fail(ex);
            }
        });
    }

    @Test
    public void should_create_mysql_schema(TestContext context) {
        Async async = context.async();
        vertx.eventBus().<JsonObject>send(Database.MYSQL_CREATE_SCHEMA, new JsonObject(), msg -> {
            try {
                JsonObject body = msg.result().body();
                context.assertNotNull(body);
                context.assertEquals(true, body.getBoolean("active"));
                context.assertEquals(true, body.getBoolean("creation"));

                async.complete();
            } catch (Exception ex) {
                context.fail(ex);
            }
        });
    }
}
