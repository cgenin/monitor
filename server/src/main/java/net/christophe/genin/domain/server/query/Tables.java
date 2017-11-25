package net.christophe.genin.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.command.Raw;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.json.Jsons;

import java.util.List;
import java.util.Optional;

public class Tables extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Raw.class);

    public static final String LIST = Tables.class.getName() + ".list";

    @Override
    public void start() throws Exception {

        vertx.eventBus().consumer(LIST, msg -> {
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
                                .put(Schemas.Tables.services.name(), doc.get(Schemas.Tables.latestUpdate.name()))
                                .put(Schemas.Tables.services.name(), services);
                    })
                    .collect(Jsons.Collectors.toJsonArray());
            msg.reply(l);
        });

        logger.info("started");
    }
}
