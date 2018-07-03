package net.christophe.genin.monitor.domain.server.db.mysql;

import rx.Single;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;


public class AntiMonitorSchemas {

    /**
     * Methods for creating an schema.
     *
     * @return The results.
     */
    public static Single<String> create() {
        Mysqls mysqls = Mysqls.Instance.get();
        return Single.just("/db/migration/V1.0.0__CREATE_SCHEMA.sql")
                .map(p -> {
                    try {
                        return AntiMonitorSchemas.class.getResource(p).toURI();
                    } catch (URISyntaxException e) {
                        throw new IllegalStateException("err in creating path", e);
                    }
                })
                .map(Paths::get)
                .map(path -> {
                    try {
                        return Files.readAllLines(path);
                    } catch (IOException e) {
                        throw new IllegalStateException("err in reading file", e);
                    }
                })
                .map(lines -> lines.stream().collect(Collectors.joining(" ")))
                .map(content -> Arrays.stream(content.split(";"))
                        .map(String::trim)
                        .toArray(String[]::new)
                )
                .flatMap(mysqls::batch)
                .map(list -> "Creation of EVENTS, PROJECTS, TABLES, API, VERSIONS if not exist :" + list);

    }


}
