package net.christophe.genin.monitor.domain.server.query;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.Database;
import net.christophe.genin.monitor.domain.server.adapter.Adapters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(VertxUnitRunner.class)
public class DependencyQueryTest {


    public static final String PATH_DB = "target/testDependencyQueryTest.db";
    Vertx vertx;


    @Before
    public void before(TestContext context) throws IOException {
        Files.deleteIfExists(Paths.get(new File(PATH_DB).toURI()));
        JsonObject config = new JsonObject().put("nitritedb", new JsonObject().put("path", PATH_DB));
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(config);

        vertx = Vertx.vertx();
        Async async = context.async(3);
        vertx.deployVerticle(Database.class.getName(), options, (result) -> {
            context.assertTrue(result.succeeded());
            async.countDown();
            Adapters.get().dependencyHandler().create("resource", "domain")
                    .subscribe(bool -> {
                        context.assertTrue(bool);
                        async.countDown();
                    });
        });
        vertx.deployVerticle(DependencyQuery.class.getName(), options, (r) -> {
            context.assertTrue(r.succeeded());
            async.countDown();
        });
    }


    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void should_find_all_resource(TestContext context) {
        Async async = context.async();
        vertx.eventBus().<JsonArray>send(DependencyQuery.FIND, new JsonObject(), msg -> {
            context.assertTrue(msg.succeeded());
            JsonArray body = msg.result().body();
            context.assertNotNull(body);
            context.assertEquals(1, body.size());
            context.assertEquals("resource", body.getString(0));
            async.complete();
        });
    }

    @Test
    public void should_used_by_resource(TestContext context) {
        Async async = context.async();
        vertx.eventBus().<JsonArray>send(DependencyQuery.USED_BY, "resource", msg -> {
            context.assertTrue(msg.succeeded());
            JsonArray body = msg.result().body();
            context.assertNotNull(body);
            context.assertEquals(1, body.size());
            context.assertEquals("domain", body.getString(0));
            async.complete();
        });
    }

    @Test
    public void should_used_by_unknown_resource(TestContext context) {
        Async async = context.async();
        vertx.eventBus().<JsonArray>send(DependencyQuery.USED_BY, "resource_unkown", msg -> {
            context.assertTrue(msg.succeeded());
            JsonArray body = msg.result().body();
            context.assertNotNull(body);
            context.assertEquals(0, body.size());
            async.complete();
        });
    }
}
