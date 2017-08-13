package net.christophe.genin.domain.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.apps.ProjectUpdater;
import net.christophe.genin.domain.server.apps.Raw;

/**
 * Main Verticle
 */
public class Server extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start() throws Exception {
        logger.info("start ....");
        vertx.deployVerticle(new Http());
        vertx.deployVerticle(new InitializeDb(), as ->{
            if(as.failed()){
                throw new IllegalStateException("Error in creating DB", as.cause());
            }
            vertx.deployVerticle(new ProjectUpdater());
            vertx.deployVerticle(new Projects());
        });
        vertx.deployVerticle(new Raw());
        logger.info("start : OK");
    }
}
