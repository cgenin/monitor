package net.christophe.genin.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.command.Raw;
import net.christophe.genin.domain.server.db.Queries;

public class Tables extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Raw.class);

    public static final String LIST = Tables.class.getName() + ".list";
    public static final String BY_PROJECT = Tables.class.getName() + ".by.project";

    @Override
    public void start() {

        vertx.eventBus().consumer(LIST, msg -> {
            Queries.get().tables()
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("error in " + LIST, err);
                                msg.fail(500, "Error in query");
                            }
                    );

        });

        vertx.eventBus().consumer(BY_PROJECT, msg->{
           Queries.get().tablesByProjects()
                   .subscribe(
                           msg::reply,
                           err -> {
                               logger.error("error in " + BY_PROJECT, err);
                               msg.fail(500, "Error in query");
                           }
                   );
        });

        logger.info("started");
    }
}
