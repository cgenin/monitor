package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.Console;
import net.christophe.genin.domain.server.db.Commands;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import org.dizitart.no2.NitriteCollection;

public class Reset extends AbstractVerticle {


    private static final Logger logger = LoggerFactory.getLogger(Reset.class);
    public static final String RUN = Reset.class.getName() + ".run";


    @Override
    public void start() {
        vertx.eventBus().consumer(RUN, (msg) -> {
            logger.info("reset ....");
            Commands.get().reset()
                    .subscribe(
                            str -> {
                                logger.info(str);
                                vertx.eventBus().send(Console.INFO, str);
                                NitriteCollection raws = Dbs.instance.getCollection(Schemas.RAW_COLLECTION);
                                raws.find().forEach(doc -> raws.update(doc.put(Schemas.RAW_STATE, Treatments.PROJECTS.getState())));
                                logger.info("treatments relaunched");
                                msg.reply(new JsonObject().put("rest", true));
                                logger.info("reset end.");
                            },
                            err -> {
                                logger.error("Error in " + RUN, err);
                                msg.fail(500, "Error in resetting");
                            });

        });

        logger.info("started");
    }
}
