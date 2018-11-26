package net.christophe.genin.domain.monitor.addon.verticles;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.AbstractVerticle;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(VertxUnitRunner.class)
public class MustacheVerticleBuilderTest {


    @Rule
    public RunTestOnContext rule = new RunTestOnContext();


    @Test
    public void should_register_template_classpath_on_starting_with_cache(TestContext context) {
        Vertx vertx = rule.vertx();
        Async async = context.async(2);
        AbstractVerticle verticle = MustacheVerticleBuilder.create()
                .withClasspathTemplate("/mustache/classpath.hbs")
                .cache(false)
                .build();
        vertx.deployVerticle(verticle, testClasspath(vertx, async));

    }


    @Test
    public void should_register_template_classpath_on_starting(TestContext context) {
        Vertx vertx = rule.vertx();
        Async async = context.async(2);
        AbstractVerticle verticle = MustacheVerticleBuilder.create().withClasspathTemplate("/mustache/classpath.hbs").build();
        vertx.deployVerticle(verticle, testClasspath(vertx, async));

    }

    @Test
    public void should_register_template_system_on_starting(TestContext context) {
        Vertx vertx = rule.vertx();
        Async async = context.async(2);
        AbstractVerticle verticle = MustacheVerticleBuilder.create().fromFileSystemTemplates("src/test/resources/mustache/system.hbs").build();
        vertx.deployVerticle(verticle, testFileSystem(vertx, async));

    }

    @Test
    public void should_register_template_folder_on_starting(TestContext context) {
        Vertx vertx = rule.vertx();
        Async async = context.async(3);
        AbstractVerticle verticle = MustacheVerticleBuilder.create().fromFileSystemFolder("src/test/resources/mustache").build();
        vertx.deployVerticle(verticle, m -> {
            testFileSystem(vertx, async).handle(m);
            testClasspath(vertx, async).handle(m);
        });

    }


    private Handler<AsyncResult<String>> testFileSystem(Vertx vertx, Async async) {
        return m -> {
            async.countDown();
            JsonObject payload = new JsonObject().put("name", "system.hbs").put("datas", new JsonObject().put("hero", "Luke Skywalker"));
            vertx.eventBus().<String>send(MustacheVerticle.COMPILE, payload, msg -> {
                assertThat(msg.succeeded()).isTrue();
                assertThat(msg.result().body()).isEqualTo("<div>Jedi : Luke Skywalker</div>");
                async.countDown();
            });
        };
    }

    private Handler<AsyncResult<String>> testClasspath(Vertx vertx, Async async) {
        return m -> {
            async.countDown();
            JsonObject payload = new JsonObject().put("name", "classpath.hbs").put("datas", new JsonObject().put("name", "Leia"));
            vertx.eventBus().<String>send(MustacheVerticle.COMPILE, payload, msg -> {
                assertThat(msg.succeeded()).isTrue();
                assertThat(msg.result().body()).isEqualTo("<div>\n" +
                        "    princess Leia\n" +
                        "</div>");
                async.countDown();
            });
        };
    }

}
