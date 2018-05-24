package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.server.Database;
import net.christophe.genin.monitor.domain.server.ReadJsonFiles;
import net.christophe.genin.monitor.domain.server.model.Raw;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@RunWith(VertxUnitRunner.class)
public class TablesCommandTest implements ReadJsonFiles {

    private static JsonObject data;


    public static final String PATH_DB = "target/testTablesCommandTest.db";
    Vertx vertx;

    @Before
    public void before(TestContext context) throws Exception {
        Files.deleteIfExists(Paths.get(new File(PATH_DB).toURI()));
        JsonObject config = new JsonObject().put("nitritedb", new JsonObject().put("path", PATH_DB));
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(config);
        vertx = Vertx.vertx();
        vertx.deployVerticle(Database.class.getName(), options, context.asyncAssertSuccess());

        data = load("/datas/projects-1.json");

    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void should_create_table_if_not_exist(TestContext context) {
        Func1<Raw, Observable<? extends String>> run = new TablesCommand().run();
        Async async = context.async(2);
        MockRaw mockRaw = new MockRaw(data, 500, context, async);
        run.call(mockRaw).subscribe(
                str -> {
                    context.assertEquals("Table 'SOCIETE' for 'artifactId' creation true", str);
                    async.countDown();
                },
                context::fail
        );
    }


    public static class MockRaw implements Raw {


        private final JsonObject json;
        private final long update;
        private final TestContext context;
        private final Async async;

        public MockRaw(JsonObject json, long update, TestContext context, Async async) {
            this.json = json;
            this.update = update;
            this.context = context;
            this.async = async;
        }


        @Override
        public JsonObject json() {
            return json;
        }

        @Override
        public Treatments state() {
            return Treatments.TABLES;
        }

        @Override
        public String artifactId() {
            return "artifactId";
        }

        @Override
        public long update() {
            return update;
        }

        @Override
        public long id() {
            return 666;
        }

        @Override
        public Single<Boolean> updateState(Treatments treatments) {
            context.assertEquals(Treatments.VERSION, treatments);
            async.countDown();
            return Single.just(true);
        }
    }
}
