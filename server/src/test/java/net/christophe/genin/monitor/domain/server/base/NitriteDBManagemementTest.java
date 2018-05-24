package net.christophe.genin.monitor.domain.server.base;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NitriteDBManagemementTest {

    private final String path;
    private final String directory;

    public NitriteDBManagemementTest(Class<?> clazz) {
        this.directory = "target/nitrite-test-dbs";
        this.path = this.directory + "/test" + clazz.getSimpleName() + ".db";
    }


    public void deleteDbsBefore() throws IOException {
        System.out.println("deleted " + Files.deleteIfExists(Paths.get(new File(path).toURI())));
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();
    }

    public DeploymentOptions getOption() throws IOException {
        return new DeploymentOptions()
                .setConfig(new JsonObject().put("nitritedb", new JsonObject().put("path", path)));
    }

    public DeploymentOptions deleteAndGetOption() throws Exception {
        deleteDbsBefore();
        return getOption();
    }
}
