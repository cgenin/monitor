package net.christophe.genin.domain.server.json;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface Jsons {

    Stream<JsonObject> toStream();

    static Jsons builder(JsonArray arr) {
        return new JsonsArray(arr);
    }

    class JsonsArray implements Jsons {
        private final JsonArray arr;

        private JsonsArray(JsonArray arr) {
            this.arr = arr;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Stream<JsonObject> toStream() {
            return Optional.ofNullable(arr).orElse(new JsonArray())
                    .getList().stream()
                    .map(o -> {
                        if (o instanceof JsonObject) {
                            return o;
                        }

                        if (o instanceof Map) {
                            return new JsonObject((Map<String, Object>) o);
                        }
                        return new JsonObject(o.toString());
                    });
        }
    }
}
