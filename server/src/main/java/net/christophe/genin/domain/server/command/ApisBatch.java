package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.json.Jsons;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;

import java.util.Optional;

import static org.dizitart.no2.filters.Filters.and;
import static org.dizitart.no2.filters.Filters.eq;

public class ApisBatch extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ApisBatch.class);

    @Override
    public void start() throws Exception {
        new Treatments.Periodic(this, logger).run(this::periodic);
        logger.info("started");
    }

    private synchronized boolean periodic() {
        final NitriteCollection collection = Dbs.instance
                .getCollection(Schemas.RAW_COLLECTION);
        collection
                .find(eq(Schemas.RAW_STATE, Treatments.URL.getState()))
                .toList()
                .stream()
                .findFirst().ifPresent(doc -> {
            final JsonObject json = Dbs.Raws.toJson(doc);
            final JsonObject apis = json.getJsonObject(Schemas.Raw.apis.name());
            if (apis != null) {
                String artifactId = apis.getString(Schemas.Raw.Apis.artifactId.name(), "");
                String groupId = apis.getString(Schemas.Raw.Apis.groupId.name(), "");
                String version = apis.getString(Schemas.Raw.Apis.version.name(), "");
                final long update = json.getLong(Schemas.Raw.update.name());
                JsonArray services = apis.getJsonArray(Schemas.Raw.Apis.services.name(), new JsonArray());
                final NitriteCollection apiCollection = Dbs.instance
                        .getCollection(Schemas.Apis.collection());
                Jsons.builder(services)
                        .toStream()
                        .flatMap(obj -> {
                            String className = obj.getString(Schemas.Raw.Apis.Services.name.name(), "");
                            JsonArray methods = obj.getJsonArray(Schemas.Raw.Apis.Services.methods.name(), new JsonArray());
                            return Jsons.builder(methods)
                                    .toStream()
                                    .map(o -> o.put("className", className));
                        })
                        .forEach(methodJson -> {
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
                            }
                        });


            }
            collection.update(doc.put(Schemas.RAW_STATE, Treatments.END.getState()));
        });
        return true;
    }
}
