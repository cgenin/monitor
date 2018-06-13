package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.Database;
import net.christophe.genin.monitor.domain.server.adapter.Adapters;
import net.christophe.genin.monitor.domain.server.base.DbWithSchemaTest;
import net.christophe.genin.monitor.domain.server.ReadJsonFiles;
import net.christophe.genin.monitor.domain.server.base.NitriteDBManagemementTest;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.server.model.Raw;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class MysqlRawCommandTest extends DbWithSchemaTest implements ReadJsonFiles {


    private static DeploymentOptions option;
    private JsonObject data;
    Vertx vertx;

    @BeforeClass
    public static void first() throws Exception {
        option = new NitriteDBManagemementTest(MysqlRawCommandTest.class).deleteAndGetOption();
    }

    @Before
    public void before(TestContext context) throws Exception {

        vertx = Vertx.vertx();
        Async async = context.async(4);
        vertx.deployVerticle(Database.class.getName(), option, (result) -> {
            context.assertTrue(result.succeeded());
            async.countDown();
            setAntiMonitorDS(context, async, vertx);
            Adapters.type.setType(Adapters.Type.MYSQL);
        });
        vertx.deployVerticle(RawCommand.class.getName(), option, (r) -> {
            context.assertTrue(r.succeeded());
            async.countDown();
        });

        data = load("/datas/projects-1.json");
    }


    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void should_save_raw_datas(TestContext context) {
        Async async = context.async(2);

        vertx.eventBus().<Long>send(RawCommand.SAVING, data, msg -> {
            context.assertTrue(msg.succeeded());
            Long id = msg.result().body();
            context.assertTrue(id > 0L);
            async.countDown();
            should_find_raw_after_creation(context, async);
        });
    }

    private void should_find_raw_after_creation(TestContext context, Async async) {
        Raw.findAll().subscribe(r -> {
            System.out.println(r);
            context.assertEquals("societe-service-impl", r.artifactId());
            context.assertEquals(Treatments.PROJECTS, r.state());
            context.assertTrue(r.update() > 1L);
            JsonObject copy = data.copy();
            copy.remove("update");
            r.json().remove("update");
            context.assertEquals(copy, r.json());
            async.countDown();
        }, context::fail);
    }


}
