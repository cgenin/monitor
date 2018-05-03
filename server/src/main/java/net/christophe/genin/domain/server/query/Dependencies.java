package net.christophe.genin.domain.server.query;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import net.christophe.genin.domain.server.db.Queries;

public class Dependencies extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Dependencies.class);

    public static final String FIND = Dependencies.class.getName() + ".find";
    public static final String USED_BY = Dependencies.class.getName() + ".usedBY";

    @Override
    public void start() {
        vertx.eventBus().consumer(FIND, msg ->
                Queries.get().listAllResourceDependencies()
                        .subscribe(
                                msg::reply,
                                err -> {
                                    logger.error("error in " + FIND, err);
                                    msg.fail(500, "Error in query");
                                }
                        ));
        vertx.eventBus().<String>consumer(USED_BY,  msg -> {
            String resource = msg.body();
            Queries.get().usedBy(resource)
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
