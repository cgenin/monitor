package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.Console;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import org.dizitart.no2.NitriteCollection;

import static org.dizitart.no2.filters.Filters.eq;

public class ProjectBatch extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ProjectBatch.class);

    @Override
    public void start() {
        new Treatments.Periodic(this, logger).run(this::periodic);
    }

    private synchronized boolean periodic() {

        final NitriteCollection collection = Dbs.instance
                .getCollection(Schemas.RAW_COLLECTION);
        collection
                .find(eq(Schemas.RAW_STATE, Treatments.PROJECTS.getState()))
                .toList()
                .stream()
                .findFirst().ifPresent(doc -> {
            final JsonObject json = Dbs.Raws.toJson(doc);
            final String artifactId = json.getString(Schemas.Raw.artifactId.name());

            // Dans tous les cas marqués le doc comme traiter.
            Commands.get().projects(json, artifactId).subscribe(
                    str -> {
                        logger.info(str);
                        vertx.eventBus().send(Console.INFO, str);
                    },
                    err -> {
                        logger.error("Error in projects batch", err);
                        collection.update(doc.put(Schemas.RAW_STATE, Treatments.TABLES.getState()));
                    },
                    () -> {
                        logger.debug("Project complete");
                        collection.update(doc.put(Schemas.RAW_STATE, Treatments.TABLES.getState()));
                    });
            ;
        });

        return true;
    }


}
