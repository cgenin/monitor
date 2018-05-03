package net.christophe.genin.domain.web.app.plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.maven.plugin.logging.Log;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

public class ReadPackageJson {
    private final static Gson gson = new GsonBuilder().create();
    private final String path;
    private final Log log;

    public ReadPackageJson(String path, Log log) {
        this.path = path;
        this.log = log;
    }

    @SuppressWarnings("unchecked")
    public Optional<Map<String, Object>> load() {
        try {
            Path p = Paths.get(path);
            if (Files.exists(p)) {
                return Files.readAllLines(p)
                        .stream()
                        .reduce((a, b) -> a + "\n" + b)
                        .map(str -> gson.fromJson(str, Map.class));
            }
        } catch (Exception e) {
            log.error(e);
        }

        return Optional.empty();
    }
}
