package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.json.Jsons;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import rx.functions.Action0;

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

            final long update = json.getLong(Schemas.Raw.update.name());

            Action0 completed = () -> collection.update(doc.put(Schemas.RAW_STATE, Treatments.VERSION.getState()));
            Commands.get().tables(listTables, artifactId, update)
                    .subscribe(
                            str -> logger.info(str),
                            err -> {
                                logger.error("error in tables for "+json.encode(), err);
                                completed.call();
                            },
                            completed
                    );
        });

        return true;
    }

}
