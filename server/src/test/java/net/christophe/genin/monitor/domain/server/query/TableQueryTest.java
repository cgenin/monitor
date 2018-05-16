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
import net.christophe.genin.monitor.domain.server.model.Table;
import net.christophe.genin.monitor.domain.server.query.TableQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(VertxUnitRunner.class)
public class TableQueryTest {

    public static final String PATH_DB = "target/testTableQueryTest.db";
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
            Table table = Adapters.get().tableHandler().newInstance();
            table.setLastUpdated(0L)
                    .setService("domain")
                    .setTableName("MyTable")
                    .create()
                    .subscribe(bool -> {
                        context.assertTrue(bool);
                        async.countDown();
                    });
        });
        vertx.deployVerticle(TableQuery.class.getName(), options, (r) -> {
            context.assertTrue(r.succeeded());
            async.countDown();
        });
    }


    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void should_find_all_tables(TestContext context) {
        Async async = context.async();
        vertx.eventBus().<JsonArray>send(TableQuery.LIST, new JsonObject(), msg-> {
            context.assertTrue(msg.succeeded());
            JsonArray body = msg.result().body();
            context.assertNotNull(body);
            context.assertEquals(1, body.size());
            context.assertEquals("{\"id\":\"MyTable\",\"name\":\"MyTable\",\"latestUpdate\":0,\"services\":[\"domain\"]}", body.getJsonObject(0).encode());
            async.complete();
        });

    }

    @Test
    public void should_count_by_project(TestContext context) {
        Async async = context.async();
        vertx.eventBus().<JsonObject>send(TableQuery.BY_PROJECT, new JsonObject(), msg-> {
            context.assertTrue(msg.succeeded());
            JsonObject body = msg.result().body();
            context.assertNotNull(body);
            context.assertEquals("{\"domain\":1}", body.encode());
            async.complete();
        });

    }
}
