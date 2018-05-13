package net.christophe.genin.monitor.domain.server.query;

import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import net.christophe.genin.monitor.domain.server.model.Dependency;

public class DependencyQuery extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(DependencyQuery.class);

    public static final String FIND = DependencyQuery.class.getName() + ".find";
    public static final String USED_BY = DependencyQuery.class.getName() + ".usedBY";

    @Override
    public void start() {
        vertx.eventBus().consumer(FIND, msg ->
                Dependency.findAllResource()
                        .distinct()
                        .reduce(new JsonArray(), JsonArray::add)
                        .subscribe(
                                msg::reply,
                                err -> {
                                    logger.error("error in " + FIND, err);
                                    msg.fail(500, "Error in query");
                                }
                        ));
        vertx.eventBus().<String>consumer(USED_BY,  msg -> {
            String resource = msg.body();
            Dependency.usedBy(resource)
                    .reduce(new JsonArray(), JsonArray::add)
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("error in " + USED_BY, err);
                                msg.fail(500, "Error in query");
                            }
                    );
        });
        logger.info("started");
    }
}
