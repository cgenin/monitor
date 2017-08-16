package net.christophe.genin.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.command.Raw;
import net.christophe.genin.domain.server.db.Dbs;
import net.christophe.genin.domain.server.db.Schemas;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Projects extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Raw.class);

    public static final String LIST = Projects.class.getName() + ".list";

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(LIST, msg -> {
            final List<JsonObject> l = Dbs.instance.getCollection(Schemas.Projects.collection())
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
                                .put(Schemas.Projects.id.name(), doc.getId().getIdValue())
                                .put(Schemas.Projects.name.name(), doc.get(Schemas.Projects.name.name()))
                                .put(Schemas.Projects.latestUpdate.name(), doc.get(Schemas.Projects.latestUpdate.name()))
                                .put(Schemas.Projects.tables.name(), attributes.toJsonArray(Schemas.Projects.tables.name()))
                                .put(Schemas.Projects.javaDeps.name(), attributes.toJsonArray(Schemas.Projects.javaDeps.name()));
                    }).collect(Collectors.toList());
            msg.reply(new JsonArray(l));
        });
        logger.info("started");
    }
}
