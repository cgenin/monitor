package net.christophe.genin.domain.monitor.addon.verticles;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(VertxUnitRunner.class)
public class MustacheVerticleTest {

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    @Before
    public void before(TestContext context) {
        Vertx vertx = rule.vertx();
        vertx.deployVerticle(new MustacheVerticle(), context.asyncAssertSuccess());
    }

    @Test
    public void should_register_template(TestContext context) {
        Async async = context.async();
        Vertx vertx = rule.vertx();
        JsonObject payload = new JsonObject().put("name", "test").put("raw", "<div>{{essai}}</div>");
        vertx.eventBus().<JsonObject>send(MustacheVerticle.REGISTER, payload, msg -> {
            assertThat(msg.succeeded()).isTrue();
            JsonObject body = msg.result().body();
            assertThat(body.getBoolean("result")).isTrue();
            async.countDown();
        });
    }

    @Test
    public void should_create_template(TestContext context) {
        Async async = context.async();
        Vertx vertx = rule.vertx();
        JsonObject payload = new JsonObject().put("name", "test").put("raw", "<div>{{essai}}</div>");
        vertx.eventBus().<JsonObject>send(MustacheVerticle.REGISTER, payload, m -> {
            JsonObject d = new JsonObject().put("name", "test").put("datas", new JsonObject().put("essai", "StarWars"));
            vertx.eventBus().<String>send(MustacheVerticle.COMPILE, d, msg -> {
                assertThat(msg.succeeded()).isTrue();
                assertThat(msg.result().body()).isEqualTo("<div>StarWars</div>");
                async.countDown();
            });
        });
    }

    @Test
    public void should_extract_name_from_classpath() {
        assertThat(MustacheVerticle.extractNameFromTemplate("/file.ft")).isEqualTo("file.ft");
        assertThat(MustacheVerticle.extractNameFromTemplate("/dir/file.ft")).isEqualTo("file.ft");
    }

    @Test
    public void should_extract_name_from_system() {
       assertThat(MustacheVerticle.extractNameFromSystemTemplate("/root/file.ft")).isEqualTo("file.ft");
       assertThat(MustacheVerticle.extractNameFromSystemTemplate("C:\\dir\\file.ft")).isEqualTo("file.ft");
    }
}
