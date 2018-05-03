package net.christophe.genin.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Queries;

public class Backup extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Backup.class);

    public static final String DUMP = Configuration.class.getName() + ".dump";

    @Override
    public void start() {
        vertx.eventBus().consumer(DUMP,
                msg -> Queries.get()
                        .dump()
                        .subscribe(
                                msg::reply,
                                err -> {
                                    logger.error("error in " + DUMP, err);
                                    msg.fail(500, "Error in query");
                                }
                        )
        );
    }
}
