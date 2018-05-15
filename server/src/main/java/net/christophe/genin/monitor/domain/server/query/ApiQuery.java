package net.christophe.genin.monitor.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.model.Api;

import static net.christophe.genin.monitor.domain.server.db.Schemas.Apis.*;
import static net.christophe.genin.monitor.domain.server.db.Schemas.Apis.latestUpdate;

/**
 * Read operation for Api.
 */
public class ApiQuery extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ApiQuery.class);

    public static final String FIND = ApiQuery.class.getName() + ".find";

    @Override
    public void start() {
        vertx.eventBus().consumer(FIND, msg ->
                Api.findAll()
                        .map(api -> new JsonObject()
                                .put(id.name(), api.id())
                                .put(name.name(), api.name())
                                .put(artifactId.name(), api.artifactId())
                                .put(groupId.name(), api.groupId())
                                .put(method.name(), api.method())
                                .put(returns.name(), api.returns())
                                .put(path.name(), api.path())
                                .put(params.name(), api.params())
                                .put(comment.name(), api.comment())
                                .put(since.name(), api.since())
                                .put(className.name(), api.className())
                                .put(latestUpdate.name(), api.latestUpdate()))
                        .reduce(new JsonArray(), JsonArray::add)
                        .subscribe(
                                msg::reply,
                                err -> {
                                    logger.error("error in " + FIND, err);
                                    msg.fail(500, "Error in query");
                                }
                        ));

        logger.info("started");
    }
}
