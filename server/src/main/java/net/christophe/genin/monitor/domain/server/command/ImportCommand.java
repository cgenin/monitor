package net.christophe.genin.monitor.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class ImportCommand extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ImportCommand.class);

    public static final String IMPORT = ImportCommand.class.getName() + ".import";

    @Override
    public void start() {
        vertx.eventBus().<JsonObject>consumer(IMPORT, (msg) -> {
            //TODO
//            if (NitriteDbs.instance.importFrom(msg.body())) {
//                msg.reply(new JsonObject());
//            } else {
            msg.fail(500, "Not yet refactored");
//            }
        });

    }


}
