package net.christophe.genin.domain.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.command.*;
import net.christophe.genin.domain.server.query.*;

/**
 * Main Verticle
 */
public class Server extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start() throws Exception {
        logger.info("start ....");
        vertx.deployVerticle(new Console());
        vertx.deployVerticle(new Http(), new DeploymentOptions().setConfig(config()));
        vertx.deployVerticle(new InitializeDb(), new DeploymentOptions().setConfig(config()), as -> {
            if (as.failed()) {
                throw new IllegalStateException("Error in creating DB", as.cause());
            }
            deployCommand();
            deployQuery();
        });
        logger.info("start : OK");
    }

    private void deployCommand() {
        vertx.deployVerticle(new Raw());
        vertx.deployVerticle(new Front());
        vertx.deployVerticle(new ProjectBatch(), new DeploymentOptions().setWorker(true));
        vertx.deployVerticle(new TablesBatch(), new DeploymentOptions().setWorker(true));
        vertx.deployVerticle(new VersionBatch(), new DeploymentOptions().setWorker(true));
        vertx.deployVerticle(new DependenciesBatch(), new DeploymentOptions().setWorker(true));
        vertx.deployVerticle(new ImportExport());
        vertx.deployVerticle(new ConfigurationCommand());
        vertx.deployVerticle(new Reset(), new DeploymentOptions().setWorker(true));
        vertx.deployVerticle(new ApisBatch(), new DeploymentOptions().setWorker(true));
    }

    private void deployQuery() {
        vertx.deployVerticle(new Projects(), new DeploymentOptions().setWorker(true));
        vertx.deployVerticle(new Tables(), new DeploymentOptions().setWorker(true));
        vertx.deployVerticle(new Backup(), new DeploymentOptions().setWorker(true));
        vertx.deployVerticle(new Configuration());
        vertx.deployVerticle(new Endpoints(), new DeploymentOptions().setWorker(true));
        vertx.deployVerticle(new Dependencies(), new DeploymentOptions().setWorker(true));
    }
}
