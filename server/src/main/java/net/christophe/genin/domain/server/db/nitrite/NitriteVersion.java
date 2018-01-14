package net.christophe.genin.domain.server.db.nitrite;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.Schemas;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.filters.Filters;

import java.util.List;
import java.util.Optional;

public class NitriteVersion {

    private static final Logger logger = LoggerFactory.getLogger(NitriteVersion.class);


    private final String artifactId;
    private final JsonObject json;
    private final String version;

    NitriteVersion(JsonObject json, String artifactId, String version) {
        this.json = json;
        this.artifactId = artifactId;
        this.version = version;
    }

    public boolean insert() {

        final String id = Optional.ofNullable(Dbs.instance.getCollection(Schemas.Projects.collection())
                .find(Filters.eq(Schemas.Projects.name.name(), artifactId))
                .firstOrDefault())
                .map(d -> d.get(Schemas.Projects.id.name(), String.class))
                .orElseThrow(() -> new IllegalStateException("No Data found for " + artifactId));
        final NitriteCollection versionCollection = Dbs.instance.getCollection(Schemas.Version.collection(id));
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
            return true;
        }
        return false;
    }

    private Document rawToVersion(JsonObject json, Document currentDoc, String version) {
        boolean snapshot = Commands.Projects.isSnapshot(version);
        List<String> javaDeps = Commands.Projects.extractJavaDeps(json);
        List<String> tables = Commands.Projects.extractTables(json);
        List<String> urls = Commands.Projects.extractUrls(json);
        Optional.ofNullable(json.getString(Schemas.Projects.changelog.name()))
                .ifPresent(s -> currentDoc.put(Schemas.Projects.changelog.name(), s));
        return currentDoc
                .put(Schemas.Version.isSnapshot.name(), snapshot)
                .put(Schemas.Version.javaDeps.name(), javaDeps)
                .put(Schemas.Version.tables.name(), tables)
                .put(Schemas.Version.apis.name(), urls)
                ;
    }
}
