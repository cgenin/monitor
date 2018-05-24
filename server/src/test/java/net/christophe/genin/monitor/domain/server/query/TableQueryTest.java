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
import net.christophe.genin.monitor.domain.server.base.NitriteDBManagemementTest;
import net.christophe.genin.monitor.domain.server.model.Dependency;
import net.christophe.genin.monitor.domain.server.model.Table;
import net.christophe.genin.monitor.domain.server.query.TableQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(VertxUnitRunner.class)
public class TableQueryTest {

    public static final String PATH_DB = "target/testTableQueryTest.db";
    private static DeploymentOptions option;
    Vertx vertx;


    @BeforeClass
    public static void first() throws Exception {
        option = new NitriteDBManagemementTest(TableQueryTest.class).deleteAndGetOption();
    }

    @Before
    public void before(TestContext context) throws IOException {

        vertx = Vertx.vertx();
        Async async = context.async(3);
        vertx.deployVerticle(Database.class.getName(), option, (result) -> {
            context.assertTrue(result.succeeded());
            async.countDown();

            Table table = Adapters.get().tableHandler().newInstance();

            Observable.concat(
                    Table.removeAll().toObservable().flatMap(nb -> Observable.empty()),
                    Dependency.removeAll().toObservable().flatMap(nb -> Observable.empty()),
                    table.setLastUpdated(0L)
                            .setService("domain")
                            .setTableName("MyTable")
                            .create().toObservable())
                    .subscribe(bool -> {
                        context.assertTrue(bool);
                        async.countDown();
                    });
        });
        vertx.deployVerticle(TableQuery.class.getName(), option, (r) -> {
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
        vertx.eventBus().<JsonArray>send(TableQuery.LIST, new JsonObject(), msg -> {
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
        vertx.eventBus().<JsonObject>send(TableQuery.BY_PROJECT, new JsonObject(), msg -> {
            context.assertTrue(msg.succeeded());
            JsonObject body = msg.result().body();
            context.assertNotNull(body);
            context.assertEquals("{\"domain\":1}", body.encode());
            async.complete();
        });

    }
}
