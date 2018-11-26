package net.christophe.genin.domain.monitor.addon.verticles;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.AbstractVerticle;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(VertxUnitRunner.class)
public class PdfVerticleBuilderTest {


    @Before
    public void before() {

    }

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();


    @Test
    public void should_register_template_classpath_on_starting_with_cache(TestContext context) {
        Vertx vertx = rule.vertx();
        Async async = context.async(2);
        AbstractVerticle verticle = PdfVerticleBuilder.create()
                .withClasspathTemplate("/mustache/classpath.hbs")
                .cache(false)
                .build();
        vertx.deployVerticle(verticle, testClasspath(vertx, async));

    }


    @Test
    public void should_register_template_classpath_on_starting(TestContext context) {
        Vertx vertx = rule.vertx();
        Async async = context.async(2);
        AbstractVerticle verticle = PdfVerticleBuilder.create().withClasspathTemplate("/mustache/classpath.hbs").build();
        vertx.deployVerticle(verticle, testClasspath(vertx, async));

    }

    @Test
    public void should_register_template_system_on_starting(TestContext context) {
        Vertx vertx = rule.vertx();
        Async async = context.async(2);
        AbstractVerticle verticle = PdfVerticleBuilder.create().fromFileSystemTemplates("src/test/resources/mustache/system.hbs").build();
        vertx.deployVerticle(verticle, testFileSystem(vertx, async));

    }

    @Test
    public void should_register_template_folder_on_starting(TestContext context) {
        Vertx vertx = rule.vertx();
        Async async = context.async(3);
        AbstractVerticle verticle = PdfVerticleBuilder.create().fromFileSystemFolder("src/test/resources/mustache").build();
        vertx.deployVerticle(verticle, m -> {
            testFileSystem(vertx, async).handle(m);
            testClasspath(vertx, async).handle(m);
        });

    }


    private Handler<AsyncResult<String>> testFileSystem(Vertx vertx, Async async) {
        return m -> {
            async.countDown();
            JsonObject payload = new JsonObject().put("name", "system.hbs").put("datas", new JsonObject().put("hero", "Luke Skywalker"));
            test(vertx, async, payload);
        };
    }

    private void test(Vertx vertx, Async async, JsonObject payload) {
        vertx.eventBus().<Buffer>send(PdfVerticle.COMPILE, payload, msg -> {
            assertThat(msg.succeeded()).isTrue();
            Buffer body = msg.result().body();
            assertThat(body).isNotNull().is(new Condition<>(b -> b.length() > 0, "not null"));
            async.countDown();
        });
    }

    private Handler<AsyncResult<String>> testClasspath(Vertx vertx, Async async) {
        return m -> {
            async.countDown();
            JsonObject payload = new JsonObject().put("name", "classpath.hbs").put("datas", new JsonObject().put("name", "Leia"));
            test(vertx, async, payload);
        };
    }
}
