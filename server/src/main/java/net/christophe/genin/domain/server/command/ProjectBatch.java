package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.ConfigurationDto;
import net.christophe.genin.domain.server.db.mysql.MysqlCommand;
import net.christophe.genin.domain.server.db.mysql.Mysqls;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.nitrite.commands.NitriteCommand;
import net.christophe.genin.domain.server.json.Jsons;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;
import rx.Observable;
import rx.Single;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

            Observable<String> flux = (!Mysqls.Instance.get().active()) ? new NitriteCommand().projects(json, artifactId)
                    : new MysqlCommand().projects(json, artifactId);
            // Dans tous les cas marqués le doc comme traiter.

            flux.subscribe(logger::info,
                    err -> {
                        logger.error("Error in projects batch", err);
                        collection.update(doc.put(Schemas.RAW_STATE, Treatments.TABLES.getState()));
                    },
                    () -> {
                    logger.info("Project complete");
                        collection.update(doc.put(Schemas.RAW_STATE, Treatments.TABLES.getState()));
                    });
            ;
        });

        return true;
    }


}
