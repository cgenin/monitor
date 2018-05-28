package net.christophe.genin.monitor.domain.server;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.monitor.domain.server.command.util.RawsTest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public interface ReadJsonFiles {


    default JsonObject load(String file) throws URISyntaxException, IOException {
        URI uri = RawsTest.class.getResource(file).toURI();
        Path path = Paths.get(uri);
        String str = Files.readAllLines(path).stream().collect(Collectors.joining("\n"));
        return new JsonObject(str).getJsonObject("json");
    }
}
