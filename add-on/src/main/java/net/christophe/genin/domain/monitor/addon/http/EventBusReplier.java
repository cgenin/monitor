package net.christophe.genin.domain.monitor.addon.http;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static net.christophe.genin.domain.monitor.addon.http.Https.CREATED_STATUS;
import static net.christophe.genin.domain.monitor.addon.http.Https.ERROR_STATUS;

public final class EventBusReplier {
    private static final Logger logger = LoggerFactory.getLogger(EventBusReplier.class);

    public static TypeEventbusReplier builder(Vertx vertx, RoutingContext rc) {
        return new EventbusReplierImpl(vertx, rc);
    }


    public interface TypeEventbusReplier {

        enum Type {
            JSON, ARRAY, CREATED
        }

        AdressEventbusReplier array();

        AdressEventbusReplier json();

        AdressEventbusReplier created();
    }

    public interface AdressEventbusReplier {
        BodyEventbusReplier adress(String addr);
    }

    public interface BodyEventbusReplier {
        BodyEventbusReplier handle(Handler<Boolean> result);

        void withNoBody();

        void withBodyFromRequest();

        void withBody(JsonObject obj);

        void withBody(JsonArray obj);

        void withBody(String obj);

        void withBody(Buffer obj);

    }

    /**
     * Class for transform event bus result to Http request
     */
    public static class EventbusReplierImpl implements TypeEventbusReplier, AdressEventbusReplier, BodyEventbusReplier {
        private final Vertx vertx;
        private final RoutingContext rc;
        private Type type;
        private String adress;
        private Object data;
        private Handler<Boolean> handler;


        private EventbusReplierImpl(Vertx vertx, RoutingContext rc) {
            this.vertx = vertx;
            this.rc = rc;
        }

        @Override
        public AdressEventbusReplier array() {
            this.type = Type.ARRAY;
            return this;
        }

        @Override
        public AdressEventbusReplier json() {
            this.type = Type.JSON;
            return this;
        }

        @Override
        public AdressEventbusReplier created() {
            this.type = Type.CREATED;
            return this;
        }

        @Override
        public BodyEventbusReplier adress(String addr) {
            Objects.requireNonNull(addr);
            this.adress = addr;
            return this;
        }


        @Override
        public void withNoBody() {
            run();
        }

        @Override
        public void withBodyFromRequest() {
            this.data = rc.getBodyAsJson();
            run();
        }

        @Override
        public void withBody(JsonObject obj) {
            Objects.requireNonNull(obj);
            this.data = obj;
            run();
        }

        @Override
        public void withBody(JsonArray obj) {
            Objects.requireNonNull(obj);
            this.data = obj;
            run();
        }

        @Override
        public void withBody(String obj) {
            Objects.requireNonNull(obj);
            this.data = obj;
            run();
        }

        @Override
        public void withBody(Buffer obj) {
            Objects.requireNonNull(obj);
            this.data = obj;
            run();
        }

        public BodyEventbusReplier handle(Handler<Boolean> result) {
            this.handler = result;
            return this;
        }

        private Consumer<?> getConsumer() {
            switch (type) {
                case JSON:
                    return (Consumer<JsonObject>) (jsonArray) -> new Https.Json(rc).send(jsonArray);
                case ARRAY:
                    return (Consumer<JsonArray>) (jsonArray) -> new Https.Json(rc).send(jsonArray);
                case CREATED:
                    return (reply) -> rc.response().setStatusCode(CREATED_STATUS).end();
                default:
                    throw new IllegalStateException("result not found " + type);
            }
        }

        private void run() {
            Object localData = Optional.ofNullable(data).orElse(new JsonObject());
            Consumer<?> consumer = getConsumer();
            consume(localData, consumer);

        }

        private <T> void consume(Object obj, Consumer<T> consumer) {
            vertx.eventBus()
                    .send(adress, obj, new DeliveryOptions(), (Handler<AsyncResult<Message<T>>>) (reply) -> {
                        if (reply.succeeded()) {
                            T jsonArray = reply.result().body();
                            consumer.accept(jsonArray);
                            Optional.ofNullable(handler).ifPresent(h -> h.handle(true));
                            return;
                        }
                        logger.error("Error - " + adress, reply.cause());
                        rc.response().setStatusCode(ERROR_STATUS).end();
                        Optional.ofNullable(handler).ifPresent(h -> h.handle(false));

                    });
        }


    }
}
