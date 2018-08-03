package net.christophe.genin.domain.monitor.addon.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

public class TestVerticle extends AbstractVerticle {


    public static final JsonObject DATA = new JsonObject().put("test", true);
    public static final String JSON = TestVerticle.class.getName() + ".json";
    public static final String ARRAY = TestVerticle.class.getName() + ".array";
    public static final String ERROR = TestVerticle.class.getName() + ".error";

    @Override
    public void start() {
        vertx.eventBus().consumer(JSON, msg -> {
            if (Objects.isNull(msg.body())) {
                msg.fail(500, "Error body is null");
                return;
            }
            msg.reply(DATA);
        });
        vertx.eventBus().consumer(ARRAY, msg -> {
            if (Objects.isNull(msg.body())) {
                msg.fail(500, "Error body is null");
                return;
            }
            msg.reply(new JsonArray().add(DATA));
        });
        vertx.eventBus().consumer(ERROR, msg -> {
                msg.fail(500, "REply null");
        });
    }
}
