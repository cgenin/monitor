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

    static final String CONTENT_TYPE_JSON = "application/json";
    static final String NO_CACHE = "private, no cache";
    static final Integer CREATED_STATUS = 204;
    static final int ERROR_STATUS = 500;




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
