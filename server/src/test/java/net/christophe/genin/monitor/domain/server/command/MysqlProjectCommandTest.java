package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.Database;
import net.christophe.genin.monitor.domain.server.DbTest;
import net.christophe.genin.monitor.domain.server.DbWithSchemaTest;
import net.christophe.genin.monitor.domain.server.ReadJsonFiles;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(VertxUnitRunner.class)
public class MysqlProjectCommandTest extends DbWithSchemaTest implements ReadJsonFiles {

    private JsonObject data;


    public static final String PATH_DB = "target/testProjectCommandTest.db";
    Vertx vertx;

    @Before
    public void before(TestContext context) throws Exception {
        Files.deleteIfExists(Paths.get(new File(PATH_DB).toURI()));
        JsonObject config = new JsonObject().put("nitritedb", new JsonObject().put("path", PATH_DB));
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(config);
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
    public void should_create_project_if_update_time_is_after_0(TestContext context) {
        Async async = context.async(3);

        ProjectCommandTest.MockRaw raw = new ProjectCommandTest.MockRaw(context, async, 500, data);
        ProjectCommandTest.create_project_if_update_time_is_after_0(context, async, raw);
    }

    @Test
    public void should_create_project_if_update_time_is_before_0(TestContext context) {
        ProjectCommandTest.create_project_if_update_time_is_before_0(context);
    }

}
