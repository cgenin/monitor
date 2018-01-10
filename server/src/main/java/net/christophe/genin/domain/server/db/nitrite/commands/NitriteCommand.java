package net.christophe.genin.domain.server.db.nitrite.commands;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.command.TablesBatch;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import rx.Observable;
import rx.Single;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.dizitart.no2.filters.Filters.eq;

public class NitriteCommand implements Commands {

    private static final Logger logger = LoggerFactory.getLogger(NitriteCommand.class);

    @Override
    public Observable<String> projects(JsonObject json, String artifactId) {
        if (new NitriteProject(json, artifactId).insert()) {
            return Observable.just("Projects '" + artifactId + "' updated");
        }
        return Observable.just("Projects '" + artifactId + "' not updated");
    }


    @SuppressWarnings("unchecked")
    @Override
    public Observable<String> tables(List<String> tables, String artifactId, long update) {
        if (tables.isEmpty())
            return Observable.empty();

        final NitriteCollection tablesCollection = Dbs.instance.getCollection(Schemas.Tables.collection());
        tables.stream()
                .map(tableName ->
                        Optional.ofNullable(tablesCollection
                                .find(eq(Schemas.Tables.name.name(), tableName))
                                .firstOrDefault()
                        ).orElseGet(
                                () -> Document.createDocument(Schemas.Projects.latestUpdate.name(), 0L)
                                        .put(Schemas.Tables.name.name(), tableName)
                                        .put(Schemas.Tables.id.name(), Dbs.newId())
                        )
                )
                .filter(document -> {
                    final Long lUpdate = Long.valueOf(document.get(Schemas.Projects.latestUpdate.name()).toString());
                    return lUpdate < update;
                })
                .peek(document -> {
                    final List<String> list = Optional.ofNullable(document.get(Schemas.Tables.services.name(), List.class))
                            .orElse(new ArrayList<String>());
                    final HashSet<String> strings = new HashSet<>(list);
                    strings.add(artifactId);
                    final ArrayList<String> results = new ArrayList<>(strings);
                    results.sort(String::compareTo);
                    document.put(Schemas.Tables.services.name(), results);
                    document.put(Schemas.Tables.latestUpdate.name(), update);
                })
                .forEach(document -> {
                    logger.info("New data for " + document.getId().getIdValue() + ". Document must be updated.");
                    tablesCollection.update(document, true);
                });
        return Observable.from(tables).map(tableName -> "Table '" + tableName + "' with " + artifactId);
    }

    @Override
    public boolean versions(JsonObject json, String artifactId) {
        return new NitriteVersion(json, artifactId).insert();
    }
}
