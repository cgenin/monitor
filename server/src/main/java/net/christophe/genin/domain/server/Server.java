package net.christophe.genin.domain.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.command.*;
import net.christophe.genin.domain.server.query.Configuration;
import net.christophe.genin.domain.server.query.Projects;
import net.christophe.genin.domain.server.query.Tables;

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
            deployCommand();
            deployQuery();

        });
        logger.info("start : OK");
    }

    private void deployCommand() {
        vertx.deployVerticle(new Raw());
        vertx.deployVerticle(new ProjectBatch());
        vertx.deployVerticle(new TablesBatch());
        vertx.deployVerticle(new VersionBatch());
        vertx.deployVerticle(new Import());
        vertx.deployVerticle(new ConfigurationCommand());
    }

    private void deployQuery() {
        vertx.deployVerticle(new Projects());
        vertx.deployVerticle(new Tables());
        vertx.deployVerticle(new Configuration());
    }
}
