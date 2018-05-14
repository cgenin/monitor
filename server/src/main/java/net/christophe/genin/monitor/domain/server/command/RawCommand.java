package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.model.Raw;

public class RawCommand extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(RawCommand.class);

    public static final String SAVING = RawCommand.class.getName() + ".saving";


    @Override
    public void start() {
        logger.info("start RawCommand Verticle");
        vertx.eventBus().<JsonObject>consumer(SAVING, rc -> {
            final JsonObject body = rc.body();
            Raw.save(body)
                    .subscribe(
                            rc::reply,
                            err -> {
                                logger.error("Error in saving events", err);
                                rc.fail(500, "Error in saving events");
                            }
                    );
        });

    }
}
