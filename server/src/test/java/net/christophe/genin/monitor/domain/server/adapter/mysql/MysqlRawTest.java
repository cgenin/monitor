package net.christophe.genin.monitor.domain.server.adapter.mysql;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.Database;
import net.christophe.genin.monitor.domain.server.ReadJsonFiles;
import net.christophe.genin.monitor.domain.server.adapter.Adapters;
import net.christophe.genin.monitor.domain.server.base.DbTest;
import net.christophe.genin.monitor.domain.server.base.DbWithSchemaTest;
import net.christophe.genin.monitor.domain.server.base.NitriteDBManagemementTest;
import net.christophe.genin.monitor.domain.server.command.MysqlTablesCommandTest;
import net.christophe.genin.monitor.domain.server.command.Treatments;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.server.model.Raw;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

@RunWith(VertxUnitRunner.class)
public class MysqlRawTest extends DbWithSchemaTest implements ReadJsonFiles {
    private static JsonObject data;


    private static DeploymentOptions options;
    Vertx vertx;

    @BeforeClass
    public static void first() throws Exception {
        options = new NitriteDBManagemementTest(MysqlTablesCommandTest.class).deleteAndGetOption();
    }

    @Before
    public void before(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        Async async = context.async(2);
        vertx.deployVerticle(Database.class.getName(), options, msg -> {
            context.assertTrue(msg.succeeded());
            Mysqls res = Mysqls.Instance.set(vertx, DbTest.config.put("user", DbTest.USER_DB));
            context.assertTrue(res.active());
            Adapters.type.setType(Adapters.Type.MYSQL);
            setAntiMonitorDS(context, async, vertx);
        });

        data = load("/datas/projects-1.json");

    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }


    @Test
    public void should_save_raw(TestContext context) {

        Async async = context.async(4);
        Raw.save(data)
                .doOnSuccess(i -> should_find_all(async, context, i))
                .doOnSuccess(i -> should_find_by_state(async, context, i))
                .subscribe(i -> {
                    System.out.println(i);
                    context.assertTrue(i > 0L);
                    async.countDown();
                }, context::fail);
    }

    @Test
    public void should_update_state(TestContext context) {

        Async async = context.async();
        Raw.save(data)
                .flatMap(raw -> Raw.updateAllStatesBy(Treatments.TABLES))
                .subscribe(nb -> {
                    context.assertTrue(nb > 0);
                    Raw.findByStateFirst(Treatments.TABLES)
                            .subscribe(raw -> {
                                context.assertNotNull(raw);
                                async.countDown();
                            }, context::fail);
                }, context::fail);
    }

    private void should_find_all(Async async, TestContext context, long l) {
        AtomicInteger count = new AtomicInteger(0);
        Raw.findAll().subscribe(raw -> {
                    context.assertNotNull(raw.json());
                    context.assertEquals("societe-service-impl", raw.artifactId());
                    count.incrementAndGet();
                }, context::fail,
                () -> {
                    context.assertTrue(count.get() >= 1);
                    async.countDown();
                });
    }


    private void should_find_by_state(Async async, TestContext context, long l) {
        AtomicInteger countProject = new AtomicInteger(0);
        Raw.findByStateFirst(Treatments.PROJECTS)
                .subscribe(raw -> {
                            context.assertEquals(l, raw.id());
                            context.assertEquals("societe-service-impl", raw.artifactId());
                            countProject.incrementAndGet();
                        },
                        context::fail,
                        () -> {
                            context.assertEquals(1, countProject.get());
                            async.countDown();
                        });
        AtomicInteger countTable = new AtomicInteger(0);
        Raw.findByStateFirst(Treatments.VERSION)
                .subscribe(raw -> {
                            context.assertNull(raw);
                            countTable.incrementAndGet();
                        },
                        context::fail,
                        () -> {
                            context.assertEquals(0, countTable.get());
                            async.countDown();
                        });
    }
}
