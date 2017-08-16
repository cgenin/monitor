package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.db.Dbs;
import org.dizitart.no2.Document;

public class Raw extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Raw.class);

    public static final String SAVING = Raw.class.getName() + ".saving";


    @Override
    public void start() throws Exception {
        logger.info("start Raw Verticle");
        vertx.eventBus().consumer(SAVING, (Handler<Message<JsonObject>>) rc -> {
            final JsonObject body = rc.body();
            final Document document = Dbs.Raws.toDoc(body)
                   .put("state", Treatments.PROJECTS.getState());

            Dbs.instance.getCollection(Schemas.RAW_COLLECTION)
                    .insert(document);
            rc.reply(document.getId().getIdValue());
        });
    }
}
