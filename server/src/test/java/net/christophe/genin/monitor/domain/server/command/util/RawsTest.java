package net.christophe.genin.monitor.domain.server.command.util;

import io.vertx.core.json.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class RawsTest {
    private JsonObject data;

    @Test
    public void should_test_is_Snaphot_or_not() {
        assertThat(Raws.isSnapshot("1.0.0-SNAPSHOT")).isTrue();
        assertThat(Raws.isSnapshot("1.0.0")).isFalse();
    }

    @Before
    public void before() throws Exception {
        URI uri = RawsTest.class.getResource("/datas/projects-1.json").toURI();
        Path path = Paths.get(uri);
        String str = Files.readAllLines(path).stream().collect(Collectors.joining("\n"));
        data = new JsonObject(str).getJsonObject("json");
    }

    @Test
    public void should_extract_java_deps() {
        List<String> strings = Raws.extractJavaDeps(data);
        assertThat(strings)
                .isNotEmpty()
                .hasSize(17)
                .contains("starter-service-impl",
                        "starter-springboot-app",
                        "springboot-config",
                        "registration-client",
                        "societe-service-manager");
    }

    @Test
    public void should_extract_urls() {
        List<String> strings = Raws.extractUrls(data);
        assertThat(strings)
                .isNotEmpty()
                .hasSize(2)
                .contains("GET - /api/societes",
                        "GET - /api/societes/:id");
    }

    @Test
    public void should_tables() {
        List<String> strings = Raws.extractTables(data);
        assertThat(strings)
                .isNotEmpty()
                .hasSize(1)
                .contains("SOCIETE");
    }
}
