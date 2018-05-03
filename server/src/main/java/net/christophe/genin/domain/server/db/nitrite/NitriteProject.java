package net.christophe.genin.domain.server.db.nitrite;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.ConfigurationDto;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.query.Configuration;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.dizitart.no2.filters.Filters.eq;

class NitriteProject {
    private static final Logger logger = LoggerFactory.getLogger(NitriteProject.class);


    private final String artifactId;
    private final JsonObject json;

    NitriteProject(JsonObject json, String artifactId) {
        this.json = json;
        this.artifactId = artifactId;
    }

    public boolean insert() {
        final NitriteCollection projectCollection = Dbs.instance.getCollection(Schemas.Projects.collection());
        final Document document = Optional.ofNullable(projectCollection
                .find(eq(Schemas.Projects.name.name(), artifactId))
                .firstOrDefault()
        ).orElseGet(
                () -> Document.createDocument(Schemas.Projects.latestUpdate.name(), 0L)
                        .put(Schemas.Projects.id.name(), Dbs.newId())
        );
        updateFromJson(document, json).ifPresent((dc) -> {
            logger.info("New data for " + artifactId + ". Document must be updated.");
            projectCollection.update(dc, true);
        });
        return true;
    }


    private Optional<Document> updateFromJson(Document document, JsonObject json) {
        final long update = json.getLong(Schemas.Raw.update.name());
        final Long lUpdate = Long.valueOf(document.get(Schemas.Projects.latestUpdate.name()).toString());
        final String artifactId = json.getString(Schemas.Raw.artifactId.name());
        if (lUpdate < update) {

            document.put(Schemas.Projects.name.name(), artifactId);
            final String version = json.getString(Schemas.Raw.version.name());
            if (Commands.Projects.isSnapshot(version)) {
                document.put(Schemas.Projects.snapshot.name(), version);
            } else {
                document.put(Schemas.Projects.release.name(), version);
            }


            final List<String> tables = Commands.Projects.extractTables(json);
            document.put(Schemas.Projects.tables.name(), tables);
            final List<String> allDeps = Commands.Projects.extractJavaDeps(json);
            final ConfigurationDto conf = Configuration.get();

            List<String> javaFilters = conf.getJavaFilters();
            final List<String> javaDeps = allDeps.parallelStream()
                    .map(String::toUpperCase)
                    .filter(s ->
                            javaFilters.isEmpty() ||
                                    javaFilters.parallelStream()
                                            .map(String::toUpperCase)
                                            .anyMatch(s::contains)
                    ).collect(Collectors.toList());
            document.put(Schemas.Projects.javaDeps.name(), javaDeps);
            Optional.ofNullable(json.getString(Schemas.Projects.changelog.name()))
                    .ifPresent(s -> document.put(Schemas.Projects.changelog.name(), s));
            final List<String> apis = Commands.Projects.extractUrls(json);
            document.put(Schemas.Projects.apis.name(), apis);
            document.put(Schemas.Projects.latestUpdate.name(), update);
            return Optional.of(document);
        }
        logger.info("No data for " + artifactId + ". Document must not be updated: " + lUpdate + " > " + update);
        return Optional.empty();
    }

}
