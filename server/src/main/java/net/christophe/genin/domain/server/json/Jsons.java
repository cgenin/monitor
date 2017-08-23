package net.christophe.genin.domain.server.json;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Jsons {

    Stream<JsonObject> toStream();
    List<String> toListString();

    static Jsons builder(JsonArray arr) {
        return new JsonsArray(arr);
    }

    @SuppressWarnings("unchecked")
    static Object objToJson(Object o) {
        if (o instanceof JsonObject) {
            return o;
        }

        if (o instanceof Map) {
            return new JsonObject((Map<String, Object>) o);
        }
        return new JsonObject(o.toString());
    }

    interface Collectors{
        static <T> Collector<T, JsonArray, JsonArray> toJsonArray() {
            return Collector.of(JsonArray::new, JsonArray::add
                    , JsonArray::addAll);
        }
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
                    .map(Jsons::objToJson);
        }

        @SuppressWarnings("unchecked")
        public List<String> toListString(){
            final List<Object> javaFilters1 = Optional.ofNullable(arr).orElse(new JsonArray()).getList();
            return javaFilters1
                    .stream()
                    .map(Object::toString)
                    .collect(java.util.stream.Collectors.toList());

        }
    }

}
