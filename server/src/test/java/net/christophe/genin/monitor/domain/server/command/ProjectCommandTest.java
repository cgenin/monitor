package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.Database;
import net.christophe.genin.monitor.domain.server.command.util.RawsTest;
import net.christophe.genin.monitor.domain.server.model.Project;
import net.christophe.genin.monitor.domain.server.model.Raw;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@RunWith(VertxUnitRunner.class)
public class ProjectCommandTest {

    private static JsonObject data;


    public static final String PATH_DB = "target/testProjectCommandTest.db";
    Vertx vertx;

    @Before
    public void before(TestContext context) throws Exception {
        Files.deleteIfExists(Paths.get(new File(PATH_DB).toURI()));
        JsonObject config = new JsonObject().put("nitritedb", new JsonObject().put("path", PATH_DB));
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(config);
        vertx = Vertx.vertx();
        vertx.deployVerticle(Database.class.getName(), options, context.asyncAssertSuccess());

        URI uri = RawsTest.class.getResource("/datas/projects-1.json").toURI();
        Path path = Paths.get(uri);
        String str = Files.readAllLines(path).stream().collect(Collectors.joining("\n"));
        data = new JsonObject(str).getJsonObject("json");
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void should_create_project_if_update_time_is_after_0(TestContext context) {
        Async async = context.async(3);

        MockRaw raw = new MockRaw(context, async, 500);

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
        });
    }

    @Test
    public void should_create_project_if_update_time_is_before_0(TestContext context) {
        Async async = context.async(2);

        MockRaw raw = new MockRaw(context, async, -12);

        Func1<Raw, Observable<String>> build = new ProjectCommand().run();

        build.call(raw).subscribe(str -> {
            context.assertEquals("No data for artifactId. Document must not be updated: 0 > -12", str);
            async.countDown();
        });
    }

    static class MockRaw implements Raw {
        private final TestContext context;
        private final Async async;
        private final long updated;


        public MockRaw(TestContext context, Async async, long updated) {
            this.context = context;
            this.async = async;
            this.updated = updated;
        }

        @Override
        public JsonObject json() {
            return data;
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
        public long update() {
            return updated;
        }

        @Override
        public long id() {
            return 0;
        }

        @Override
        public Single<Boolean> updateState(Treatments treatments) {
            context.assertEquals(Treatments.TABLES, treatments);
            async.countDown();
            return Single.just(true);
        }
    }
}
