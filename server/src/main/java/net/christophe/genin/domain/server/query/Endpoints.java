package net.christophe.genin.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Queries;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.json.Jsons;

import static net.christophe.genin.domain.server.db.Schemas.Apis.*;

public class Endpoints extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Endpoints.class);

    public static final String FIND = Endpoints.class.getName() + ".find";

    @Override
    public void start() {
        vertx.eventBus().consumer(FIND, msg ->
                Queries.get().apis()
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
