package net.christophe.genin.domain.server;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.command.*;
import net.christophe.genin.domain.server.db.Nitrite2Mysql;
import net.christophe.genin.domain.server.db.migration.MigrateConfiguration;
import net.christophe.genin.domain.server.db.migration.MigrateInQueue;
import net.christophe.genin.domain.server.query.*;

import java.util.ArrayList;

/**
 * Main Verticle
 */
public class Server extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String HTTP = "http";
    private static final String MIGRATION = "migration";
    public static final String STOP = Server.class.getName() + ".stop";
    public static final String FAIL = Server.class.getName() + ".fail";


    @Override
    public void start() {
        String app = config().getString("app", HTTP);
        switch (app) {
            case MIGRATION:
                migration();
                break;
            case HTTP:
            default:
                server();
        }

    }


    private void migration() {
        logger.info("migration start ....");

        Stopping stopping = new Stopping(vertx);
        vertx.eventBus().consumer(STOP, stopping.stopping(0));
        vertx.eventBus().consumer(FAIL, stopping.stopping(1));

        vertx.deployVerticle(new MigrateConfiguration(), stopping.register());
        vertx.deployVerticle(new MigrateInQueue(),new DeploymentOptions().setConfig(config()).setWorker(true),  stopping.register());
        vertx.deployVerticle(new Nitrite2Mysql(), new DeploymentOptions().setConfig(config()), stopping.register());
    }

    private void server() {
        logger.info("http start ....");
        vertx.deployVerticle(new Console());
        vertx.deployVerticle(new Http(), new DeploymentOptions().setConfig(config()));
        vertx.deployVerticle(new InitializeDb(), new DeploymentOptions().setConfig(config()), as -> {
            if (as.failed()) {
                throw new IllegalStateException("Error in creating DB", as.cause());
            }
            deployCommand();
            deployQuery();
        });
        logger.info("http verticles launch : OK");
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

    private static class Stopping {
        private final ArrayList<String> deployements = new ArrayList<>();
        private final Vertx vertx;

        Stopping(Vertx vertx) {
            this.vertx = vertx;
        }

        private Handler<AsyncResult<String>> register() {
            return msg -> {
                String result = msg.result();
                logger.info("id " + result + " created");
                deployements.add(result);
            };
        }

        private Handler<Message<Object>> stopping(int exitCode) {
            return msg -> {
                deployements.forEach(id -> vertx.undeploy(id, end -> logger.info("destroying " + id)));
                vertx.close();
                System.exit(exitCode);
                logger.info("Main verticle close.");
            };
        }
    }
}
