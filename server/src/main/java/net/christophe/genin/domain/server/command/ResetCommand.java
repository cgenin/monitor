package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.Console;
import net.christophe.genin.domain.server.model.*;
import rx.Observable;

public class ResetCommand extends AbstractVerticle {


    private static final Logger logger = LoggerFactory.getLogger(ResetCommand.class);
    public static final String RUN = ResetCommand.class.getName() + ".run";


    @Override
    public void start() {
        vertx.eventBus().consumer(RUN, (msg) -> {
            logger.info("reset ....");
            Observable.concat(
                    Version.removeAll().map(nb -> "Version's deleted : " + nb).toObservable(),
                    Dependency.removeAll().map(nb -> "Version's deleted : " + nb).toObservable(),
                    Api.removeAll().map(nb -> "Api's deleted : " + nb).toObservable(),
                    Project.removeAll().map(nb -> "Project's deleted : " + nb).toObservable(),
                    Table.removeAll().map(nb -> "Table's deleted : " + nb).toObservable()
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
                                Raw.updateAllStatesBy(Treatments.PROJECTS)
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
