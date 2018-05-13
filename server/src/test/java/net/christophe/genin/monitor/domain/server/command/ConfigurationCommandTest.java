package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.model.Configuration;
import org.junit.Test;
import rx.functions.Func1;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationCommandTest {

    @Test
    public void should_set_json_to_configuration() {
        JsonObject mysql = new JsonObject()
                .put("host", "A")
                .put("port", 9999)
                .put("user", "dark")
                .put("password", "vador")
                .put("database", "EtoileNoir");
        JsonArray javaFilters = new JsonArray().add("-truc");
        JsonArray npmFilters = new JsonArray().add("anNpmModule");
        JsonObject body = new JsonObject()
                .put("id", 666L)
                .put("mysql", mysql)
                .put("javaFilters", javaFilters)
                .put("npmFilters", npmFilters);
        Func1<Configuration, Configuration> func = new ConfigurationCommand().setDatas(body);
        assertThat(func).isNotNull();
        Configuration result = func.call(new MockConfiguration());
        assertThat(result.mysqlUser()).isEqualToIgnoringCase("dark");
        assertThat(result.javaFilters()).hasSize(1).contains("-truc");
        assertThat(result.mysqlDB()).isEqualToIgnoringCase("EtoileNoir");
        assertThat(result.mysqlHost()).isEqualToIgnoringCase("A");
        assertThat(result.mysqlPassword()).isEqualToIgnoringCase("vador");
        assertThat(result.npmFilters()).hasSize(1).contains("anNpmModule");
        assertThat(result.mysqlPort()).isEqualTo(9999);

    }

    public static class MockConfiguration extends Configuration {}
}
