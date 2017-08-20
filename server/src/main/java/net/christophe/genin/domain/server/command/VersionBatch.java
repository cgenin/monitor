package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.json.Jsons;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.filters.Filters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.dizitart.no2.filters.Filters.eq;

public class VersionBatch extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(VersionBatch.class);

    @Override
    public void start() throws Exception {
        new Treatments.Periodic(this, logger).run(this::periodic);
    }


    private synchronized boolean periodic() {

        final NitriteCollection collection = Dbs.instance
                .getCollection(Schemas.RAW_COLLECTION);
        collection
                .find(eq(Schemas.RAW_STATE, Treatments.VERSION.getState()))
                .toList()
                .stream()
                .findFirst().ifPresent(doc -> {
            final JsonObject json = Dbs.Raws.toJson(doc);
            final String artifactId = json.getString(Schemas.Raw.artifactId.name());
            final String id = Optional.ofNullable(Dbs.instance.getCollection(Schemas.Projects.collection())
                    .find(Filters.eq(Schemas.Projects.name.name(), artifactId))
                    .firstOrDefault())
                    .map(d -> d.get(Schemas.Projects.id.name(), String.class))
                    .orElseThrow(() -> new IllegalStateException("No Data found for " + artifactId));
            final NitriteCollection versionCollection = Dbs.instance.getCollection(Schemas.Version.collection(id));
            final String version = json.getString(Schemas.Raw.version.name());
            Document currentDoc = Optional.ofNullable(versionCollection
                    .find(Filters.eq(Schemas.Version.name.name(), version))
                    .firstOrDefault())
                    .orElseGet(() -> Document.createDocument(Schemas.Version.latestUpdate.name(), 0L)
                            .put(Schemas.Version.name.name(), version)
                            .put(Schemas.Version.id.name(), Dbs.newId())
                    );

            long lDate = currentDoc.get(Schemas.Version.latestUpdate.name(), Long.class);
            long update = json.getLong(Schemas.Raw.update.name());
            if (lDate < update) {
                logger.info("New data for " + currentDoc.getId().getIdValue() + ". Document must be updated.");
                versionCollection.update(rawToVersion(json, currentDoc, version)
                        .put(Schemas.Version.latestUpdate.name(), update), true);
            }
            collection.update(doc.put(Schemas.RAW_STATE, Treatments.END.getState()));
        });

        return true;
    }

    private Document rawToVersion(JsonObject json, Document currentDoc, String version) {
        boolean snapshot = ProjectBatch.isSnapshot(version);
        List<String> javaDeps = ProjectBatch.extractJavaDeps(json);
        List<String> tables = ProjectBatch.extractTables(json);
        return currentDoc
                .put(Schemas.Version.isSnapshot.name(), snapshot)
                .put(Schemas.Version.javaDeps.name(), javaDeps)
                .put(Schemas.Version.tables.name(), tables)
                ;
    }


}
