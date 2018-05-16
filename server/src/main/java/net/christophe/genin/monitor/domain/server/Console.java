package net.christophe.genin.monitor.domain.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Date;

/**
 * Verticle for the push message to  all console clients.
 */
public class Console extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(Console.class);
    public static final String INFO = Console.class.getName() + ".info";
    static final String CONSOLE = "console.text";


    @Override
    public void start() {
        vertx.eventBus().<String>consumer(INFO, (msg) -> {
            long date = new Date().getTime();
            vertx.eventBus().publish(CONSOLE, new JsonObject()
                    .put("msg", msg.body())
                    .put("date", date)
                    .put("type", "info"));

        });
        logger.info("started");
    }
}
