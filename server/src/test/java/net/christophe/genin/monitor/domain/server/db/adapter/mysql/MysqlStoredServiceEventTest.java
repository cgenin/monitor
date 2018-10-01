package net.christophe.genin.monitor.domain.server.adapter.mysql;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.Database;
import net.christophe.genin.monitor.domain.server.adapter.Adapters;
import net.christophe.genin.monitor.domain.server.base.DbWithSchemaTest;
import net.christophe.genin.monitor.domain.server.base.NitriteDBManagemementTest;
import net.christophe.genin.monitor.domain.server.verticle.command.MysqlRawCommandTest;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.StoredServiceEvent;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IStoredServiceEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)

public class MysqlStoredServiceEventTest extends DbWithSchemaTest {

    private Vertx vertx;
    private DeploymentOptions option;


    @Before
    public void before(TestContext context) throws Exception {
        option = new NitriteDBManagemementTest(MysqlRawCommandTest.class).deleteAndGetOption();

        vertx = Vertx.vertx();
        Async async = context.async(3);
        vertx.deployVerticle(Database.class.getName(), option, (result) -> {
            context.assertTrue(result.succeeded());
            setAntiMonitorDS(context, async, vertx);
            async.countDown();
        });
    }

    @Test
    public void should_store_events(TestContext context) {
        IStoredServiceEvent IStoredServiceEvent = Adapters.get().storedServiceEventHandler();
        StoredServiceEvent sse = IStoredServiceEvent.newInstance(new JsonObject().put("test", true), 666L);
        context.assertNotNull(sse);
        Async async = context.async();
        sse.create()
                .subscribe(b -> {
                    context.assertTrue(b);
                    async.countDown();
                }, context::fail);
    }
}
