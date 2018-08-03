package net.christophe.genin.domain.monitor.addon.http;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Utils class for https.
 */
public final class Https {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String NO_CACHE = "private, no cache";
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
