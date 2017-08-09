package net.christophe.genin.domain.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Main Verticle
 */
public class Server extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start() throws Exception {
        logger.info("start ....");
        vertx.deployVerticle(new Http());
        vertx.deployVerticle(new Db());
        logger.info("start : OK");
    }
}
