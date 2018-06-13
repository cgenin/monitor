package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.Database;
import net.christophe.genin.monitor.domain.server.ReadJsonFiles;
import net.christophe.genin.monitor.domain.server.base.DbTest;
import net.christophe.genin.monitor.domain.server.base.DbWithSchemaTest;
import net.christophe.genin.monitor.domain.server.base.NitriteDBManagemementTest;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class MysqlTablesCommandTest extends DbWithSchemaTest implements ReadJsonFiles {

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
        Async async = context.async();
        vertx.deployVerticle(Database.class.getName(), options, msg -> {
            context.assertTrue(msg.succeeded());
            Mysqls res = Mysqls.Instance.set(vertx, DbTest.config.put("user", DbTest.USER_DB));
            context.assertTrue(res.active());
            async.complete();
        });

        data = load("/datas/projects-1.json");

    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void should_create_table_if_not_exist(TestContext context) {
        Async async = context.async(2);
        TablesCommandTest.MockRaw mockRaw = new TablesCommandTest.MockRaw(data, 500, context, async);
        TablesCommandTest.test_should_create_table_if_not_exist(context, data, mockRaw, async);
    }


}
