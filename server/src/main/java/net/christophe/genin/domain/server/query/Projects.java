package net.christophe.genin.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Queries;

public class Projects extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Projects.class);

    public static final String LIST = Projects.class.getName() + ".list";
    public static final String GET = Projects.class.getName() + ".get";
    public static final String ID = "id";


    @Override
    public void start() {
        vertx.eventBus().consumer(LIST,
                msg -> Queries.get().projects()
                        .subscribe(
                                msg::reply,
                                err -> {
                                    logger.error("error in " + LIST, err);
                                    msg.fail(500, "Error in query");
                                }
                        ));
        vertx.eventBus().<JsonObject>consumer(GET,  msg -> {
            String id = msg.body().getString(ID, "");
            Queries.get().versions(id)
                    .subscribe(
                            msg::reply,
                            err -> {
                                logger.error("error in " + GET, err);
                                msg.fail(500, "Error in query");
                            }
                    );
        });

        logger.info("started");
    }
}
