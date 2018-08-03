package net.christophe.genin.monitor.domain.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.base.DbTest;
import net.christophe.genin.monitor.domain.server.base.NitriteDBManagemementTest;
import net.christophe.genin.monitor.domain.server.db.mysql.FlywayVerticle;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;


@RunWith(VertxUnitRunner.class)
public class MysqlDatabaseTest extends DbTest {


    private static DeploymentOptions option;
    private Vertx vertx;


    @BeforeClass
    public static void first() throws Exception {
        option = new NitriteDBManagemementTest(MysqlDatabaseTest.class).deleteAndGetOption();
    }

    @Before
    public void before(TestContext context) {
        vertx = Vertx.vertx();
        Async async = context.async(3);

        Observable.concat(
                vertx.rxDeployVerticle(Database.class.getName(), option).toObservable(),
                vertx.rxDeployVerticle(FlywayVerticle.class.getName(), option).toObservable()
        ).reduce("", (acc, s) -> s)
                .subscribe(r -> {
                    async.countDown();
                    setAntiMonitorDS(context, async, vertx);

                }, context::fail);
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
