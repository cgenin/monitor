package net.christophe.genin.domain.server.db.nitrite;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.monitor.addon.json.Jsons;
import net.christophe.genin.domain.server.db.Queries;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.query.Projects;
import rx.Single;

import java.util.List;
import java.util.Optional;

import static net.christophe.genin.domain.server.db.Schemas.Apis.*;
import static net.christophe.genin.domain.server.db.Schemas.Apis.latestUpdate;

public class NitriteQuery implements Queries {
    private static final Logger logger = LoggerFactory.getLogger(Projects.class);


    @Override
    public Single<JsonArray> projects() {
        final JsonArray arr = Dbs.instance.getCollection(Schemas.Projects.collection())
                .find().toList()
                .parallelStream()
                .map(doc -> {
                    final JsonObject obj = new JsonObject();
                    Optional.ofNullable(doc.get(Schemas.Projects.release.name()))
                            .ifPresent((s) -> obj.put(Schemas.Projects.release.name(), s));
                    Optional.ofNullable(doc.get(Schemas.Projects.snapshot.name()))
                            .ifPresent((s) -> obj.put(Schemas.Projects.snapshot.name(), s));
                    final Dbs.Attributes attributes = new Dbs.Attributes(doc);
                    return obj
                            .put(Schemas.Projects.id.name(), doc.get(Schemas.Projects.id.name()))
                            .put(Schemas.Projects.name.name(), doc.get(Schemas.Projects.name.name()))
                            .put(Schemas.Projects.latestUpdate.name(), doc.get(Schemas.Projects.latestUpdate.name()))
                            .put(Schemas.Projects.tables.name(), attributes.toJsonArray(Schemas.Projects.tables.name()))
                            .put(Schemas.Projects.apis.name(), attributes.toJsonArray(Schemas.Projects.apis.name()))
                            .put(Schemas.Projects.changelog.name(), doc.get(Schemas.Projects.changelog.name()))
                            .put(Schemas.Projects.javaDeps.name(), attributes.toJsonArray(Schemas.Projects.javaDeps.name()));
                }).collect(Jsons.Collectors.toJsonArray());
        return Single.just(arr);
    }

    @Override
    public Single<JsonArray> tables() {
        final JsonArray l = Dbs.instance.getCollection(Schemas.Tables.collection())
                .find().toList()
                .parallelStream()
                .map(doc -> {
                    JsonArray services = Optional.ofNullable(doc.get(Schemas.Tables.services.name(), List.class))
                            .map(JsonArray::new).orElse(new JsonArray());

                    return new JsonObject()
                            .put(Schemas.Tables.id.name(), doc.getId().getIdValue())
                            .put(Schemas.Tables.name.name(), doc.get(Schemas.Tables.name.name()))
                            .put(Schemas.Tables.latestUpdate.name(), doc.get(Schemas.Tables.latestUpdate.name()))
                            .put(Schemas.Tables.services.name(), services);
                })
                .collect(Jsons.Collectors.toJsonArray());
        return Single.just(l);
    }

    @Override
    public Single<JsonArray> apis() {
        return Single.fromCallable(() -> Dbs.instance
                .getCollection(Schemas.Apis.collection())
                .find().toList()
                .parallelStream()
                .map(doc -> new JsonObject()
                        .put(id.name(), doc.get(id.name()))
                        .put(name.name(), doc.get(name.name()))
                        .put(artifactId.name(), doc.get(artifactId.name()))
                        .put(groupId.name(), doc.get(groupId.name()))
                        .put(method.name(), doc.get(method.name()))
                        .put(returns.name(), doc.get(returns.name()))
                        .put(path.name(), doc.get(path.name()))
                        .put(params.name(), doc.get(params.name()))
                        .put(comment.name(), doc.get(comment.name()))
                        .put(since.name(), doc.get(since.name()))
                        .put(className.name(), doc.get(className.name()))
                        .put(latestUpdate.name(), doc.get(latestUpdate.name())))
                .collect(Jsons.Collectors.toJsonArray()));
    }

    @Override
    public Single<JsonArray> versions(String idProject) {
        final JsonArray l = Dbs.instance.getCollection(Schemas.Version.collection())
                .find().toList()
                .parallelStream()
                .map(doc -> {
                    final Dbs.Attributes attributes = new Dbs.Attributes(doc);
                    return new JsonObject()
                            .put(Schemas.Version.id.name(), doc.get(Schemas.Version.id.name()))
                            .put(Schemas.Version.name.name(), doc.get(Schemas.Version.name.name()))
                            .put(Schemas.Version.isSnapshot.name(), doc.get(Schemas.Version.isSnapshot.name()))
                            .put(Schemas.Version.changelog.name(), doc.get(Schemas.Version.changelog.name()))
                            .put(Schemas.Version.latestUpdate.name(), doc.get(Schemas.Version.latestUpdate.name()))
                            .put(Schemas.Version.tables.name(), attributes.toJsonArray(Schemas.Version.tables.name()))
                            .put(Schemas.Version.apis.name(), attributes.toJsonArray(Schemas.Version.apis.name()))
                            .put(Schemas.Version.javaDeps.name(), attributes.toJsonArray(Schemas.Version.javaDeps.name()));
                }).collect(Jsons.Collectors.toJsonArray());
        if (logger.isDebugEnabled()) {
            logger.debug("GET : " + idProject + " -res :" + l.encodePrettily());
        }
        return Single.just(l);
    }

    @Override
    public Single<JsonArray> listAllResourceDependencies() {
        // TODO Implements
        return Single.just(new JsonArray());
    }

    @Override
    public Single<JsonArray> usedBy(String resource) {
        // TODO Implements
        return Single.just(new JsonArray());
    }

    @Override
    public Single<JsonObject> tablesByProjects() {
        // TODO Implements
        return Single.just(new JsonObject());
    }

    @Override
    public Single<JsonObject> dump() {
        // TODO Implements
        return Single.just(new JsonObject());
    }
}
