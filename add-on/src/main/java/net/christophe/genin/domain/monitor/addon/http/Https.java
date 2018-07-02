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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public final class Https {

    private static final Logger logger = LoggerFactory.getLogger(Https.class);
    static final String CONTENT_TYPE_JSON = "application/json";
    static final String NO_CACHE = "private, no cache";
    static final Integer CREATED_STATUS = 204;
    static final int ERROR_STATUS = 500;

    static Integer toStatusCreated(AsyncResult as) {
        return toStatus(as, CREATED_STATUS);
    }

    private static Integer toStatus(AsyncResult reply, int okStatus) {
        return Optional.of(reply)
                .filter(AsyncResult::succeeded)
                .map(r -> okStatus)
                .orElseGet(() -> {
                    logger.error("Error", reply.cause());
                    return ERROR_STATUS;
                });
    }

    /**
     * Class for transform event bus result to Http request
     */
    public static class EbCaller {
        private final Vertx vertx;
        private final RoutingContext rc;
        private Handler<Boolean> handler;

        public EbCaller(Vertx vertx, RoutingContext rc) {
            this.vertx = vertx;
            this.rc = rc;
        }

        private <T> EbCaller consume(String addr, Object obj, Consumer<T> consumer) {
            Objects.requireNonNull(addr);
            vertx.eventBus()
                    .send(addr, obj, new DeliveryOptions(), (Handler<AsyncResult<Message<T>>>) (reply) -> {
                        if (reply.succeeded()) {
                            T jsonArray = reply.result().body();
                            consumer.accept(jsonArray);
                            Optional.ofNullable(handler).ifPresent(h -> h.handle(true));
                            return;
                        }
                        logger.error("Error - " + addr, reply.cause());
                        rc.response().setStatusCode(ERROR_STATUS).end();
                        Optional.ofNullable(handler).ifPresent(h -> h.handle(false));

                    });
            return this;
        }

        public EbCaller handle(Handler<Boolean> result) {
            this.handler = result;
            return this;
        }

        public EbCaller created(String addr, JsonObject data) {
            vertx.eventBus()
                    .send(addr, data, new DeliveryOptions(), (Handler<AsyncResult<Message<JsonArray>>>) (reply) -> {
                        final Integer status = Https.toStatusCreated(reply);
                        rc.response().setStatusCode(status).end();
                        boolean state = CREATED_STATUS.equals(status);
                        Optional.ofNullable(handler).ifPresent(h -> h.handle(state));
                    });
            return this;
        }

        public EbCaller arrAndReply(String addr) {
            return arrAndReply(addr, new JsonObject());
        }

        public EbCaller arrAndReply(String addr, JsonObject data) {
            Objects.requireNonNull(data);
            return consume(addr, data, (Consumer<JsonArray>) (jsonArray) -> new Https.Json(rc).send(jsonArray));
        }

        public EbCaller arrAndReply(String addr, String data) {
            Objects.requireNonNull(data);
            return consume(addr, data, (Consumer<JsonArray>) (jsonArray) -> new Https.Json(rc).send(jsonArray));
        }

        public EbCaller arrAndReply(String addr, Buffer data) {
            Objects.requireNonNull(data);
            return consume(addr, data, (Consumer<JsonArray>) (jsonArray) -> new Https.Json(rc).send(jsonArray));
        }

        public EbCaller jsonAndReply(String addr, JsonObject data) {
            Objects.requireNonNull(data);
            return consume(addr, data, (Consumer<JsonObject>) (obj) -> new Https.Json(rc).send(obj));
        }

        public EbCaller jsonAndReply(String addr) {
            return jsonAndReply(addr, new JsonObject());
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
                    .setStatusCode(200)
                    .putHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .putHeader(HttpHeaders.CACHE_CONTROL, NO_CACHE)
                    .end(data);
        }
    }
}
