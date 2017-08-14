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

import java.util.*;
import java.util.stream.Collectors;

import static org.dizitart.no2.filters.Filters.eq;

public class TablesBatch extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(TablesBatch.class);

    @Override
    public void start() throws Exception {
        new Treatments.Periodic(this, logger).run(this::periodic);
    }


    private synchronized boolean periodic() {

        final NitriteCollection collection = Dbs.instance
                .getCollection(Schemas.RAW_COLLECTION);
        collection
                .find(eq(Schemas.RAW_STATE, Treatments.TABLES.getState()))
                .toList()
                .stream()
                .findFirst().ifPresent(doc -> {
            final JsonObject json = Dbs.Raws.toJson(doc);
            final List<String> listTables = Jsons.builder(json.getJsonArray(Schemas.Raw.Tables.collection())).toStream()
                    .map(js -> js.getString(Schemas.Raw.Tables.table.name(), ""))
                    .collect(Collectors.toList());
            final String artifactId = json.getString(Schemas.Raw.artifactId.name());

            final NitriteCollection tablesCollection = Dbs.instance.getCollection(Schemas.Tables.collection());
            final long update = json.getLong(Schemas.Raw.update.name());

            listTables.stream()
                    .map(tableName ->
                            Optional.ofNullable(tablesCollection
                                    .find(eq(Schemas.Tables.name.name(), tableName))
                                    .firstOrDefault()
                            ).orElseGet(
                                    () -> Document.createDocument(Schemas.Projects.latestUpdate.name(), 0L)
                                            .put(Schemas.Tables.name.name(), tableName)
                            )
                    )
                    .filter(document -> {
                        final Long lUpdate = Long.valueOf(document.get(Schemas.Projects.latestUpdate.name()).toString());
                        return lUpdate < update;
                    })
                    .map(document -> updateDocument(artifactId, update, document))
                    .forEach(document -> {
                        logger.info("New data for " + document.getId().getIdValue() + ". Document must be updated.");
                        tablesCollection.update(document, true);
                    });
            ;

            collection.update(doc.put(Schemas.RAW_STATE, Treatments.END.getState()));
        });

        return true;
    }


    @SuppressWarnings("unchecked")
    private Document updateDocument(String artifactId, long update, Document document) {
        final List<String> list = Optional.ofNullable(document.get(Schemas.Tables.services.name(), List.class))
                .orElse(new ArrayList<String>());
        final HashSet<String> strings = new HashSet<>(list);
        strings.add(artifactId);
        final ArrayList<String> results = new ArrayList<>(strings);
        results.sort(String::compareTo);
        document.put(Schemas.Tables.services.name(), results);
        document.put(Schemas.Tables.latestUpdate.name(), update);
        return document;
    }
}
