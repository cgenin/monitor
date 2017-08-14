package net.christophe.genin.domain.server.apps;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Dbs;
import net.christophe.genin.domain.server.db.Schemas;

import net.christophe.genin.domain.server.json.Jsons;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.dizitart.no2.filters.Filters.eq;

public class ProjectBatch extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ProjectBatch.class);

    @Override
    public void start() throws Exception {
        final long batch = Treatments.batchTime(config());
        logger.info("batch time : " + batch);
        vertx.setPeriodic(batch, (id) -> periodic());

        logger.info("started.");
    }

    private synchronized void periodic() {
        logger.debug("Début du traitement");
        final NitriteCollection collection = Dbs.instance
                .getCollection(Schemas.RAW_COLLECTION);
        collection
                .find(eq(Schemas.RAW_STATE, Treatments.PROJECTS.getState()))
                .toList()
                .stream()
                .findFirst().ifPresent(doc -> {
            final JsonObject json = Dbs.Raws.toJson(doc);
            final String artifactId = json.getString(Schemas.Raw.artifactId.name());

            final NitriteCollection projectCollection = Dbs.instance.getCollection(Schemas.Projects.collection());
            final Document document = Optional.ofNullable(projectCollection
                    .find(eq(Schemas.Projects.name.name(), artifactId))
                    .firstOrDefault()
            ).orElseGet(() -> Document.createDocument(Schemas.Projects.latestUpdate.name(), 0L));
            updateFromJson(document, json).ifPresent((dc) -> {
                logger.info("New data for " + artifactId + ". Document must be updated.");
                projectCollection.update(dc, true);
            });
            // Dans tous les cas marqués le doc comme traiter.
            collection.update(doc.put(Schemas.RAW_STATE, Treatments.TABLES.getState()));
        });
        logger.debug("Fin du traitement");
    }

    private Optional<Document> updateFromJson(Document document, JsonObject json) {
        final long update = json.getLong(Schemas.Raw.update.name());
        final Long lUpdate = Long.valueOf(document.get(Schemas.Projects.latestUpdate.name()).toString());
        final String artifactId = json.getString(Schemas.Raw.artifactId.name());
        if (lUpdate < update) {

            document.put(Schemas.Projects.name.name(), artifactId);
            final String version = json.getString(Schemas.Raw.version.name());
            if (version.contains("SNAPSHOT")) {
                document.put(Schemas.Projects.snapshot.name(), version);
            } else {
                document.put(Schemas.Projects.release.name(), version);
            }

            final List<String> tables = Jsons.builder(json.getJsonArray(Schemas.Raw.Tables.collection())).toStream()
                    .map(js -> js.getString(Schemas.Raw.Tables.table.name(), ""))
                    .collect(Collectors.toList());
            document.put(Schemas.Projects.tables.name(), tables);
            final List<String> javaDeps = Jsons.builder(json.getJsonArray(Schemas.Raw.Dependencies.collection())).toStream()
                    //                            Todo Filtering .filter
                    .map(js -> js.getString(Schemas.Raw.Dependencies.artifactId.name(), ""))
                    .collect(Collectors.toList());
            document.put(Schemas.Projects.javaDeps.name(), javaDeps);

            document.put(Schemas.Projects.latestUpdate.name(), update);
            return Optional.of(document);
        }
        logger.info("No data for " + artifactId + ". Document must not be updated: " + lUpdate + " > " + update);
        return Optional.empty();
    }

}
