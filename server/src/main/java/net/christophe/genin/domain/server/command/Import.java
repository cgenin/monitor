package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.db.nitrite.Dbs;

public class Import extends AbstractVerticle {

    public static final String IMPORT = Import.class.getName() + ".import";

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(IMPORT, (Handler<Message<JsonObject>>) (msg) -> {
            if (Dbs.instance.importFrom(msg.body())) {
                msg.reply(new JsonObject());
            } else {
                msg.fail(500, "Error in importing");
            }
        });
    }
}
