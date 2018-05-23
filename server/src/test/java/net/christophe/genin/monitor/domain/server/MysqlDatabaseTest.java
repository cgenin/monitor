package net.christophe.genin.monitor.domain.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.adapter.nitrite.NitriteConfiguration;
import net.christophe.genin.monitor.domain.server.base.DbTest;
import net.christophe.genin.monitor.domain.server.base.NitriteDBManagemementTest;
import net.christophe.genin.monitor.domain.server.model.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(VertxUnitRunner.class)
public class MysqlDatabaseTest extends DbTest {


    public static final String PATH_DB = "target/testMysqlDatabaseTest.db";
    private static DeploymentOptions option;
    Vertx vertx;


    @BeforeClass
    public static void first() throws Exception {
        option = new NitriteDBManagemementTest(MysqlDatabaseTest.class).deleteAndGetOption();
    }

    @Before
    public void before(TestContext context) throws IOException {
        vertx = Vertx.vertx();
        Async async = context.async(3);
        vertx.deployVerticle(Database.class.getName(), option, r -> {
            async.countDown();
            Configuration conf = new NitriteConfiguration().setMysqlHost(HOST_DB)
                    .setMysqlDB(NAM_DB)
                    .setMysqlPassword(PWD_DB)
                    .setMysqlPort(PORT_DB)
                    .setMysqlUser(USER_DB);
            Configuration.save(conf).subscribe(
                    bool -> {
                        context.assertTrue(bool);
                        async.countDown();
                        vertx.eventBus().<JsonObject>send(Database.MYSQL_ON_OFF, new JsonObject(), msg -> {
                            JsonObject body = msg.result().body();
                            context.assertNotNull(body);
                            context.assertEquals(true, body.getBoolean("active"));
                            async.countDown();
                        });
                    },
                    context::fail
            );

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
