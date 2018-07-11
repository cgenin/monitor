package net.christophe.genin.monitor.domain.server.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import net.christophe.genin.monitor.domain.server.model.FrontApps;

public class FrontAppsQuery extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(FrontAppsQuery.class);

    public static final String FIND_ALL = FrontAppsQuery.class.getName() + ".find.all";


    @Override
    public void start() {

        vertx.eventBus().consumer(FIND_ALL, msg -> {
            FrontApps.findAll()
                    .map(fa -> {

                        return new JsonObject()
                                .put("id", fa.id())
                                .put("groupId", fa.groupId())
                                .put("artifactId", fa.artifactId())
                                .put("version", fa.version())
                                .put("lastUpdate", fa.lastUpdate())
                                .put("packagesJson", fa.packagesJson());
                    })
                    .reduce(new JsonArray(), JsonArray::add)
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("error", err);
                                msg.fail(500, "error");
                            });
        });

        logger.info("started");
    }
}
