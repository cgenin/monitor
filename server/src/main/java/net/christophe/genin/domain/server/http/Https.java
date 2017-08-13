package net.christophe.genin.domain.server.http;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import java.util.Optional;
import java.util.function.Consumer;

public final class Https {

    private static final Logger logger = LoggerFactory.getLogger(Https.class);

    public static Integer toStatusCreated(AsyncResult as) {
        return toStatus(as, 204);
    }

    private static Integer toStatus(AsyncResult reply, int okStatus) {
        return Optional.of(reply)
                .filter(AsyncResult::succeeded)
                .map(r -> okStatus)
                .orElseGet(() -> {
                    logger.error("Error", reply.cause());
                    return 500;
                });
    }

    public static class EbCaller {
        private final Vertx vertx;
        private final RoutingContext rc;

        public EbCaller(Vertx vertx, RoutingContext rc) {
            this.vertx = vertx;
            this.rc = rc;
        }

        public void arr(String addr, Consumer<JsonArray> consumer) {
            vertx.eventBus()
                    .send(addr, new JsonObject(), new DeliveryOptions(), (Handler<AsyncResult<Message<JsonArray>>>) (reply) -> {
                        if (reply.succeeded()) {
                            JsonArray jsonArray = reply.result().body();
                            consumer.accept(jsonArray);
                            return;
                        }
                        logger.error("Error - " + addr, reply.cause());
                        rc.response().setStatusCode(500).end();
                    });
        }

        public void arrAndReply(String addr) {
            arr(addr, (jsonArray) -> new Https.Json(rc).send(jsonArray));
        }
    }

    public static class Json {
        private final RoutingContext rc;


        public Json(RoutingContext rc) {
            this.rc = rc;
        }

        public void send(JsonArray array) {
            send(array.encode());
        }

        public void send(String data) {
            rc.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end(data);
        }
    }
}
