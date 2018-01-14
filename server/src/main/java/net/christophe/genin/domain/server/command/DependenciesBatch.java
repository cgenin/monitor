package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.Console;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.ConfigurationDto;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.query.Configuration;
import org.dizitart.no2.NitriteCollection;
import rx.functions.Action0;

import static org.dizitart.no2.filters.Filters.eq;

public class DependenciesBatch extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(DependenciesBatch.class);

    @Override
    public void start() {
        new Treatments.Periodic(this, logger).run(this::periodic);
    }


    private synchronized boolean periodic() {

        final NitriteCollection collection = Dbs.instance
                .getCollection(Schemas.RAW_COLLECTION);
        collection
                .find(eq(Schemas.RAW_STATE, Treatments.DEPENDENCIES.getState()))
                .toList()
                .stream()
                .findFirst().ifPresent(doc -> {
            final JsonObject json = Dbs.Raws.toJson(doc);
            final String artifactId = json.getString(Schemas.Raw.artifactId.name());
            ConfigurationDto configurationDto = Configuration.get();

            Action0 completed = () -> collection.update(doc.put(Schemas.RAW_STATE, Treatments.END.getState()));
            Commands.get()
                    .dependencies(json, artifactId, configurationDto)
                    .subscribe(
                            str -> {
                                logger.info(str);
                                vertx.eventBus().send(Console.INFO, str);
                            },
                            err -> {
                                logger.error("error in tables for " + json.encode(), err);
                                completed.call();
                            },
                            completed);
        });

        return true;
    }


}
