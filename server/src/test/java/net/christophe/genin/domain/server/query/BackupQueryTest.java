package net.christophe.genin.domain.server.query;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.domain.server.InitializeDb;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(VertxUnitRunner.class)
public class BackupQueryTest {


    Vertx vertx;


    @Before
    public void before(TestContext context) throws IOException {
        JsonObject config = new JsonObject().put("nitritedb", new JsonObject().put("path", "target/testBackupQuery.db"));
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(config);

        vertx = Vertx.vertx();
        Async async = context.async(2);
        vertx.deployVerticle(InitializeDb.class.getName(), options, (result) -> {
            context.assertTrue(result.succeeded());
            async.countDown();

        });
        vertx.deployVerticle(BackupQuery.class.getName(), options, (r) -> {
            context.assertTrue(r.succeeded());
            async.countDown();
        });
    }


    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void should_health_respond_after_starting(TestContext context) {
        Async async = context.async();
        vertx.eventBus().<JsonObject>send(BackupQuery.DUMP, new JsonObject(), msg -> {
            try {
                JsonObject body = msg.result().body();
                context.assertNotNull(body);
                async.complete();
            } catch (Exception ex) {
                context.fail(ex);
            }
        });
    }
}
