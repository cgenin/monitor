package net.christophe.genin.monitor.domain.server.query;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import net.christophe.genin.monitor.domain.boundedcontext.domain.ConfigurationDomain;
import net.christophe.genin.monitor.domain.boundedcontext.domain.infrastructure.IConfiguration;
import net.christophe.genin.monitor.domain.server.db.adapter.Adapters;
import net.christophe.genin.monitor.domain.server.verticle.Database;
import net.christophe.genin.monitor.domain.boundedcontext.domain.model.Configuration;
import net.christophe.genin.monitor.domain.server.verticle.MockConfiguration;
import org.dizitart.no2.objects.Id;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(VertxUnitRunner.class)
public class ConfigurationQueryTest {
    Vertx vertx;
    private Configuration configuration;
    private String result = "{\"id\":1,\"moniThorUrl\":null,\"javaFilters\":[\"onejava\"],\"npmFilters\":[\"npm\"],\"ignoreJava\":[],\"mysql\":{\"host\":\"host\",\"port\":666,\"user\":\"user\",\"password\":\"password\",\"database\":\"db\"}}";

    @Before
    public void before(TestContext context) throws IOException {
        JsonObject config = new JsonObject().put("nitritedb", new JsonObject().put("path", "target/testConfigurationQueryTest.db"));
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(config);

        vertx = Vertx.vertx();
        Async async = context.async(3);
        vertx.deployVerticle(ConfigurationQuery.class.getName(), options, (result) -> {
            context.assertTrue(result.succeeded());
            async.countDown();

        });
        configuration = new MockConfiguration()
                .setMysqlUser("user")
                .setMysqlHost("host")
                .setMysqlPort(666)
                .setMysqlPassword("password")
                .setMysqlDB("db")
                .setJavaFilters(Collections.singletonList("onejava"))
                .setNpmFilters(Collections.singletonList("npm"));

        vertx.deployVerticle(Database.class.getName(), options, (result) -> {
            context.assertTrue(result.succeeded());
            async.countDown();
            IConfiguration iConfiguration = Adapters.get().configurationHandler();
            new ConfigurationDomain(iConfiguration).save(configuration)
                    .subscribe(bool -> {
                        context.assertTrue(bool);
                        async.countDown();
                    }, context::assertNull);
        });
    }

    @Test
    public void should_get_configuration(TestContext context) {
        Async async = context.async();
        vertx.eventBus().<JsonObject>send(ConfigurationQuery.GET, new JsonObject(), msg -> {
            context.assertTrue(msg.succeeded());
            JsonObject body = msg.result().body();
            context.assertEquals(result, body.encode());
            async.complete();
        });
    }


}
