package net.christophe.genin.monitor.domain.server;

import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.adapter.Adapters;
import net.christophe.genin.monitor.domain.server.command.*;
import net.christophe.genin.monitor.domain.server.db.mysql.FlywayVerticle;
import net.christophe.genin.monitor.domain.server.migration.Nitrite2Mysql;
import net.christophe.genin.monitor.domain.server.migration.MigrateInQueue;
import net.christophe.genin.monitor.domain.server.query.*;

import java.util.ArrayList;

/**
 * Main Verticle.
 * <p>Verticle for launching all verticles switching the current configuration.</p>
 */
public class Server extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String HTTP = "http";
    private static final String MIGRATION = "migration";
    public static final String STOP = Server.class.getName() + ".stop";
    public static final String FAIL = Server.class.getName() + ".fail";
    public static final DeploymentOptions OPTIONS_WORKER = new DeploymentOptions().setWorker(true);


    @Override
    public void start() {
        String app = config().getString("app", HTTP);
        switch (app) {
            case MIGRATION:
                migration();
                break;
            case HTTP:
            default:
                logger.warn("Unkonwn mode : " + app);
                server();
        }

    }


    private void migration() {
        logger.info("migration start ....");

        Stopping stopping = new Stopping(vertx);
        vertx.eventBus().consumer(STOP, stopping.stopping(0));
        vertx.eventBus().consumer(FAIL, stopping.stopping(1));

        vertx.deployVerticle(new MigrateInQueue(), new DeploymentOptions().setConfig(config()).setWorker(true), stopping.register());
        vertx.deployVerticle(new Nitrite2Mysql(), new DeploymentOptions().setConfig(config()), stopping.register());
    }

    private void server() {
        logger.info("http start ....");
        vertx.deployVerticle(new Console());
        vertx.deployVerticle(new Http(), new DeploymentOptions().setConfig(config()));
        vertx.deployVerticle(new Adapters(), new DeploymentOptions().setConfig(config()), res ->
                vertx.deployVerticle(new Database(), new DeploymentOptions().setConfig(config()), as -> {
                    if (as.failed()) {
                        throw new IllegalStateException("Error in creating DB", as.cause());
                    }
                    deployCommand();
                    deployQuery();
                })
        );
        logger.info("http verticles launch : OK");
    }

    private void deployCommand() {
        vertx.deployVerticle(new RawCommand());
        vertx.deployVerticle(new FrontCommand());
        vertx.deployVerticle(new ProjectCommand(), OPTIONS_WORKER);
        vertx.deployVerticle(new TablesCommand(), OPTIONS_WORKER);
        vertx.deployVerticle(new VersionCommand(), OPTIONS_WORKER);
        vertx.deployVerticle(new DependenciesCommand(), OPTIONS_WORKER);
        vertx.deployVerticle(new ImportCommand());
        vertx.deployVerticle(new ArchiveCommand());
        vertx.deployVerticle(new ConfigurationCommand());
        vertx.deployVerticle(new NitriteCommand());
        vertx.deployVerticle(new FlywayVerticle());
        vertx.deployVerticle(new ResetCommand(), OPTIONS_WORKER);
        vertx.deployVerticle(new ApisCommand(), OPTIONS_WORKER);
    }

    private void deployQuery() {
        vertx.deployVerticle(new ProjectQuery(), OPTIONS_WORKER);
        vertx.deployVerticle(new TableQuery(), OPTIONS_WORKER);
        vertx.deployVerticle(new BackupQuery(), OPTIONS_WORKER);
        vertx.deployVerticle(new ConfigurationQuery());
        vertx.deployVerticle(new ApiQuery(), OPTIONS_WORKER);
        vertx.deployVerticle(new DependencyQuery(), OPTIONS_WORKER);
        vertx.deployVerticle(new FrontAppsQuery(), OPTIONS_WORKER);
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
