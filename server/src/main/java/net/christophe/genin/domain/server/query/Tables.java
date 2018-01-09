package net.christophe.genin.domain.server.query;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.command.Raw;
import net.christophe.genin.domain.server.db.Queries;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.json.Jsons;

import java.util.List;
import java.util.Optional;

public class Tables extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Raw.class);

    public static final String LIST = Tables.class.getName() + ".list";

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

        logger.info("started");
    }
}
