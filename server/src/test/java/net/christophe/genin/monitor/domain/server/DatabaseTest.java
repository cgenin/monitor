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

@RunWith(VertxUnitRunner.class)
public class DatabaseTest {


    Vertx vertx;


    @Before
    public void before(TestContext context) {
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("nitritedb", new JsonObject().put("path", "target/testInitializdb.db")));

        vertx = Vertx.vertx();
        vertx.deployVerticle(Database.class.getName(), options, context.asyncAssertSuccess());
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
}
