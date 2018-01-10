package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.nitrite.commands.NitriteCommand;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.filters.Filters;

import java.util.List;
import java.util.Optional;

import static org.dizitart.no2.filters.Filters.eq;

public class VersionBatch extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(VersionBatch.class);

    @Override
    public void start() {
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

            //new NitriteCommand().versions(json, artifactId);

            collection.update(doc.put(Schemas.RAW_STATE, Treatments.URL.getState()));
        });

        return true;
    }


}
