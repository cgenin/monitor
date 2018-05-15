package net.christophe.genin.domain.monitor.addon.http;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
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

    public static Integer toStatus(AsyncResult reply, int okStatus) {
        return Optional.of(reply)
                .filter(AsyncResult::succeeded)
                .map(r -> okStatus)
                .orElseGet(() -> {
                    logger.error("Error", reply.cause());
                    return 500;
                });
    }

    /**
     * Class for transform event bus result to Http request
     */
    public static class EbCaller {
        private final Vertx vertx;
        private final RoutingContext rc;

        public EbCaller(Vertx vertx, RoutingContext rc) {
            this.vertx = vertx;
            this.rc = rc;
        }

        private <T> void consume(String addr, Object obj, Consumer<T> consumer) {
            vertx.eventBus()
                    .send(addr, obj, new DeliveryOptions(), (Handler<AsyncResult<Message<T>>>) (reply) -> {
                        if (reply.succeeded()) {
                            T jsonArray = reply.result().body();
                            consumer.accept(jsonArray);
                            return;
                        }
                        logger.error("Error - " + addr, reply.cause());
                        rc.response().setStatusCode(500).end();
                    });
        }

        public void created(String addr, JsonObject data) {
            vertx.eventBus()
                    .send(addr, data, new DeliveryOptions(), (Handler<AsyncResult<Message<JsonArray>>>) (reply) -> {
                        final Integer status = Https.toStatusCreated(reply);
                        rc.response().setStatusCode(status).end();
                    });
        }

        public void arrAndReply(String addr) {
            arrAndReply(addr, new JsonObject());
        }

        public void arrAndReply(String addr, JsonObject data) {
            consume(addr, data, (Consumer<JsonArray>) (jsonArray) -> new Https.Json(rc).send(jsonArray));
        }

        public void arrAndReply(String addr, String data) {
            consume(addr, data, (Consumer<JsonArray>) (jsonArray) -> new Https.Json(rc).send(jsonArray));
        }

        public void arrAndReply(String addr, Buffer data) {
            consume(addr, data, (Consumer<JsonArray>) (jsonArray) -> new Https.Json(rc).send(jsonArray));
        }

        public void jsonAndReply(String addr, JsonObject data) {
            consume(addr, data, (Consumer<JsonObject>) (obj) -> new Https.Json(rc).send(obj));
        }

        public void jsonAndReply(String addr) {
            jsonAndReply(addr, new JsonObject());
        }
    }

    /**
     * Manage Json response.
     */
    public static class Json {
        private final RoutingContext rc;


        public Json(RoutingContext rc) {
            this.rc = rc;
        }

        public void send(JsonArray array) {
            send(array.encode());
        }

        public void send(JsonObject obj) {
            send(obj.encode());
        }

        public void send(String data) {
            rc.response()
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .putHeader(HttpHeaders.CACHE_CONTROL, "private, no cache")
                    .end(data);
        }
    }
}
