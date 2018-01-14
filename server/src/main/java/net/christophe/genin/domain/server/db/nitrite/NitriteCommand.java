package net.christophe.genin.domain.server.db.nitrite;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.Schemas;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import rx.Observable;
import rx.Single;

import java.util.*;

import static org.dizitart.no2.filters.Filters.and;
import static org.dizitart.no2.filters.Filters.eq;

public class NitriteCommand implements Commands {

    private static final Logger logger = LoggerFactory.getLogger(NitriteCommand.class);

    @Override
    public Single<String> reset() {
        return Single.fromCallable(() -> {
            Dbs.instance.nitrite().listCollectionNames()
                    .parallelStream()
                    .filter(s -> s.startsWith(Schemas.Version.PREFIX))
                    .forEach(name -> Dbs.instance.nitrite().getCollection(name).drop());
            logger.info("version collections deleted");
            Dbs.instance.getCollection(Schemas.Tables.collection()).drop();
            logger.info("tables collections deleted");
            Dbs.instance.getCollection(Schemas.Projects.collection()).drop();
            logger.info("project collections deleted");
            return "Datas cleared";
        });
    }

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
    public Observable<String> versions(JsonObject json, String artifactId, String version) {
        return Observable
                .fromCallable(() -> {
                    if (new NitriteVersion(json, artifactId, version).insert())
                        return "Version '" + version +
                                "' for '" + artifactId +
                                "' updated";
                    return "Version '" + version +
                            "' for '" + artifactId +
                            "' not updated";
                });
    }


    @Override
    public Observable<String> apis(JsonObject apis, String artifactId, String version, long update, JsonArray services) {
        final NitriteCollection apiCollection = Dbs.instance
                .getCollection(Schemas.Apis.collection());
        return servicesToJson(services)
                .flatMap(methodJson -> {
                    String groupId = apis.getString(Schemas.Raw.Apis.groupId.name(), "");
                    final String method = methodJson.getString("method", "");
                    final String path = methodJson.getString("path", "");
                    final Document current = Optional.ofNullable(apiCollection.find(
                            and(
                                    eq(Schemas.Apis.method.name(), method),
                                    eq(Schemas.Apis.path.name(), path),
                                    eq(Schemas.Apis.artifactId.name(), artifactId),
                                    eq(Schemas.Apis.groupId.name(), groupId)
                            )
                    ).firstOrDefault()).orElseGet(
                            () -> Document.createDocument(Schemas.Projects.id.name(), Dbs.newId())
                                    .put(Schemas.Apis.method.name(), method)
                                    .put(Schemas.Apis.path.name(), path)
                                    .put(Schemas.Apis.artifactId.name(), artifactId)
                                    .put(Schemas.Apis.groupId.name(), groupId)
                                    .put(Schemas.Apis.since.name(), version)
                                    .put(Schemas.Apis.latestUpdate.name(), 0L));

                    final Long lUpdate = Long.valueOf(current.get(Schemas.Apis.latestUpdate.name()).toString());
                    if (lUpdate < update) {
                        current
                                .put(Schemas.Apis.name.name(), methodJson.getString("name"))
                                .put(Schemas.Apis.returns.name(), methodJson.getString("returns"))
                                .put(Schemas.Apis.params.name(), methodJson.getJsonArray("params").encode())
                                .put(Schemas.Apis.comment.name(), methodJson.getString("comment"))
                                .put(Schemas.Apis.className.name(), methodJson.getString("className"));
                        apiCollection.update(current, true);
                        logger.info("Api " + method + " - " + path + " mis à jour");
                        Observable.just("URL '" + path +
                                "' for artifact '" + artifactId +
                                " updated :1");
                    }
                    return Observable.empty();
                });
    }
}
