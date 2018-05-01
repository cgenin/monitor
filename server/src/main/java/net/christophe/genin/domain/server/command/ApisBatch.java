package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.Console;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import org.dizitart.no2.NitriteCollection;
import rx.functions.Action0;

import static org.dizitart.no2.filters.Filters.eq;

public class ApisBatch extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ApisBatch.class);

    @Override
    public void start() {
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
            final JsonObject apis = json.getJsonObject(Schemas.Raw.apis.name(), new JsonObject());
            String artifactId = json.getString(Schemas.Raw.Apis.artifactId.name(), "");
            String version = json.getString(Schemas.Raw.Apis.version.name(), "");
            final long update = json.getLong(Schemas.Raw.update.name());
            JsonArray services = apis.getJsonArray(Schemas.Raw.Apis.services.name(), new JsonArray());

            Action0 completed = () -> collection.update(doc.put(Schemas.RAW_STATE, Treatments.DEPENDENCIES.getState()));
            Commands.get().apis(apis, artifactId, version, update, services)
                    .subscribe(str -> {
                                logger.info(str);
                                vertx.eventBus().send(Console.INFO, str);
                            },
                            err -> {
                                logger.error("Error in projects batch", err);
                                completed.call();
                            },
                            completed);


        });
        return true;
    }


}
