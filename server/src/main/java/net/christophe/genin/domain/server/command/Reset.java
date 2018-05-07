package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.Console;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.model.*;
import org.dizitart.no2.NitriteCollection;
import rx.Observable;

public class Reset extends AbstractVerticle {


    private static final Logger logger = LoggerFactory.getLogger(Reset.class);
    public static final String RUN = Reset.class.getName() + ".run";


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
                                Raw.updateAllStatesBy(Treatments.PROJECTS);
                                logger.info("treatments relaunched");
                                msg.reply(new JsonObject().put("rest", true));
                                logger.info("reset end.");
                            });

        });

        logger.info("started");
    }
}
