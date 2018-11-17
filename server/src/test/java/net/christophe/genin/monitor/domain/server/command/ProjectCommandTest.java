package net.christophe.genin.monitor.domain.server.command;

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
import net.christophe.genin.monitor.domain.server.base.NitriteDBManagemementTest;
import net.christophe.genin.monitor.domain.server.db.mysql.Mysqls;
import net.christophe.genin.monitor.domain.server.model.Configuration;
import net.christophe.genin.monitor.domain.server.model.Project;
import net.christophe.genin.monitor.domain.server.model.Raw;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

@RunWith(VertxUnitRunner.class)
public class ProjectCommandTest implements ReadJsonFiles {

    private static JsonObject data;


    private static DeploymentOptions option;
    Vertx vertx;

    @BeforeClass
    public static void first() throws Exception {
        option = new NitriteDBManagemementTest(ProjectCommandTest.class).deleteAndGetOption();
    }

    @Before
    public void before(TestContext context) throws Exception {
        DbTest.disabledAndSetAdapterToNitrite();
        vertx = Vertx.vertx();
        Async async = context.async(2);
        vertx.deployVerticle(Database.class.getName(), option, msg -> {
            Configuration.load().subscribe(conf -> async.countDown());
            async.countDown();
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

        MockRaw raw = new MockRaw(context, async, 500, data);
        create_project_if_update_time_is_after_0(context, async, raw);
    }

    @Test
    public void should_create_project_if_update_time_is_before_0(TestContext context) {

        create_project_if_update_time_is_before_0(context);
    }

    public static void create_project_if_update_time_is_after_0(TestContext context, Async async, MockRaw raw) {


        Func1<Raw, Observable<String>> build = new ProjectCommand().run();

        build.call(raw).subscribe(str -> {
            context.assertEquals("Project 'artifactId' updated", str);
            async.countDown();
            Project.findAll()
                    .toList()
                    .subscribe(l -> {
                        context.assertEquals(1, l.size());
                        Project project = l.get(0);
                        context.assertEquals("artifactId", project.name());
                        context.assertEquals("1.0.3-SNAPSHOT", project.snapshot());
                        context.assertNull(project.release());
                        context.assertNotNull(project.id());
                        async.countDown();
                    });
        }, context::fail);
    }


    public static void create_project_if_update_time_is_before_0(TestContext context) {
        Async async = context.async(2);

        MockRaw raw = new MockRaw(context, async, -12, data);

        Func1<Raw, Observable<String>> build = new ProjectCommand().run();

        build.call(raw).subscribe(str -> {
            context.assertTrue(str.startsWith("No data for artifactId. Document must not be updated: "));
            async.countDown();
        });
    }

    static class MockRaw implements Raw {
        private final TestContext context;
        private final Async async;
        private final long updated;
        private final JsonObject json;


        MockRaw(TestContext context, Async async, long updated, JsonObject data) {
            this.context = context;
            this.async = async;
            this.updated = updated;
            this.json = data;
        }

        @Override
        public JsonObject json() {
            return json;
        }

        @Override
        public Treatments state() {
            return Treatments.PROJECTS;
        }

        @Override
        public String artifactId() {
            return "artifactId";
        }

        @Override
        public Long update() {
            return updated;
        }


        @Override
        public Long id() {
            return 0L;
        }

        @Override
        public Single<Boolean> updateState(Treatments treatments) {
            context.assertEquals(Treatments.TABLES, treatments);
            async.countDown();
            return Single.just(true);
        }
    }
}
