package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.monitor.domain.server.Console;
import net.christophe.genin.monitor.domain.server.model.*;
import rx.Observable;

public class ResetCommand extends AbstractVerticle {


    private static final Logger logger = LoggerFactory.getLogger(ResetCommand.class);
    public static final String RUN = ResetCommand.class.getName() + ".run";


    @Override
    public void start() {
        vertx.eventBus().consumer(RUN, (msg) -> {
            logger.info("reset ....");
            Observable.concat(
                    new VersionDomain(versionDao).removeAll().map(nb -> "Version's deleted : " + nb).toObservable(),
                    new DependencyDomain(iDependency).removeAll().map(nb -> "Version's deleted : " + nb).toObservable(),
                    new ApiDomain(iApi).removeAll(),
                    new ProjectDomain(projectDao).removeAll().map(nb -> "Project's deleted : " + nb).toObservable(),
                    new TableDomain(tableDao).removeAll().map(nb -> "Table's deleted : " + nb).toObservable()
            )
                    .subscribe(
                            str -> {
                                logger.info(str);
                                vertx.eventBus().send(Console.INFO, str);

                            },
                            err -> {
                                logger.error("Error in " + RUN, err);
                                msg.fail(500, "Error in resetting");
                            },
                            () -> {
                                IRaw iRaw = Adapters.get().rawHandler();
                                new RawDomain(iRaw).reset()
                                        .subscribe(nb -> {
                                                    logger.info("treatments relaunched for " + nb);
                                                    msg.reply(new JsonObject().put("rest", true));
                                                    logger.info("reset end.");
                                                },
                                                err -> {
                                                    logger.error("Error in " + RUN + "in relaunch ", err);
                                                    msg.fail(500, "Error in resetting");
                                                });

                            });

        });

        logger.info("started");
    }
}
