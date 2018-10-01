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
import net.christophe.genin.monitor.domain.server.base.DbTest;
import net.christophe.genin.monitor.domain.server.base.NitriteDBManagemementTest;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Project;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

@RunWith(VertxUnitRunner.class)
public class ProjectQueryTest {


    public static final String PATH_DB = "target/testProjectQueryTest.db";
    private static DeploymentOptions option;
    Vertx vertx;
    private Project project;


    @BeforeClass
    public static void first() throws Exception {
        option = new NitriteDBManagemementTest(ProjectQueryTest.class).deleteAndGetOption();
    }


    @Before
    public void before(TestContext context) throws IOException {
        DbTest.disabledAndSetAdapterToNitrite();

        vertx = Vertx.vertx();
        Async async = context.async(3);
        vertx.deployVerticle(Database.class.getName(), option, (result) -> {
            context.assertTrue(result.succeeded());
            async.countDown();
            Adapters.get().projectHandler().readByName ("oneProject")
                    .map(project -> project
                            .setName("MyProject")
                            .setApis(Collections.singletonList("api"))
                            .setChangeLog("Changelog")
                            .setJavaDeps(Collections.singletonList("java"))
                            .setLatestUpdate(0L)
                            .setRelease("1.0.0")
                            .setSnapshot("1.0.1-SNAPSHOT")
                            .setTables(Collections.singletonList("table"))
                    )
                    .flatMap(p -> setProject(p).save())
                    .subscribe(bool -> {
                        context.assertTrue(bool);
                        async.countDown();
                    }, context::fail);
        });
        vertx.deployVerticle(ProjectQuery.class.getName(), option, (r) -> {
            context.assertTrue(r.succeeded());
            async.countDown();
        });
    }

    public Project setProject(Project project) {
        this.project = project;
        return project;
    }

    @After
    public void after(TestContext context) {

        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void should_find_all_projects(TestContext context) {
        Async async = context.async();
        vertx.eventBus().<JsonArray>send(ProjectQuery.LIST, new JsonObject(), msg -> {
            context.assertTrue(msg.succeeded());
            JsonArray body = msg.result().body();
            context.assertNotNull(body);
            context.assertEquals(2, body.size());
            JsonObject obj = body.getJsonObject(0);
            context.assertNotNull(obj.getString("id"));
            obj.remove("id");

            context.assertEquals("{\"release\":\"1.0.0\",\"snapshot\":\"1.0.1-SNAPSHOT\",\"name\":\"MyProject\",\"latestUpdate\":0,\"tables\":[\"table\"],\"apis\":[\"api\"],\"changelog\":\"1.0.1-SNAPSHOT\",\"javaDeps\":[\"java\"]}", obj.encode());
            async.complete();
        });
    }


    @Test
    public void should_get_one_project(TestContext context) {
        Async async = context.async();
        vertx.eventBus().<JsonArray>send(ProjectQuery.GET, new JsonObject().put(ProjectQuery.ID,project.id()), msg -> {
            JsonArray body = msg.result().body();
            context.assertNotNull(body);
            context.assertEquals(0, body.size());
            async.complete();
        });
    }
}
