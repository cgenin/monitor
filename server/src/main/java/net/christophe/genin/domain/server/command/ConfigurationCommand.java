package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.db.ConfigurationDto;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;

public class ConfigurationCommand extends AbstractVerticle {


    public static final String SAVE = ConfigurationCommand.class.getName() + ".save";

    @Override
    public void start() {
        vertx.eventBus().consumer(SAVE, (Handler<Message<JsonObject>>) (msg) -> {
            JsonObject body = msg.body();
            ConfigurationDto dto = Schemas.Configuration.fromJson(body);
            if (Dbs.instance.repository(ConfigurationDto.class)
                    .update(dto, true).getAffectedCount() == 1) {
                msg.reply(body);
            } else {
                msg.fail(500, "Error in saving configuration");
            }

        });
    }
}
