package net.christophe.genin.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.json.Jsons;

import static net.christophe.genin.domain.server.db.Schemas.Apis.*;

public class Endpoints extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Endpoints.class);

    public static final String FIND = Endpoints.class.getName() + ".find";

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(FIND, msg -> {
            final JsonArray l = Dbs.instance.getCollection(Schemas.Apis.collection())
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
                    .collect(Jsons.Collectors.toJsonArray());
            msg.reply(l);
        });

        logger.info("started");
    }
}
