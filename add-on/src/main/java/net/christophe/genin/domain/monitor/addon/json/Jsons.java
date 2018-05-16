package net.christophe.genin.domain.monitor.addon.json;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Util class for managing the JsonObject / JsonArray of Vertx project.
 */
public interface Jsons {


    /**
     * create the instance of JsonArrays.
     *
     * @param arr the JsonArray.
     * @return the util class.
     */
    static JsonsArray builder(JsonArray arr) {
        if (Objects.isNull(arr))
            return new JsonsArrayImpl(new JsonArray());
        return new JsonsArrayImpl(arr);
    }

    /**
     * Conversion of an object to JsonObject.
     *
     * @param o an object.
     * @return an JsonObject
     */
    @SuppressWarnings("unchecked")
    static JsonObject objToJson(Object o) {
        if (o instanceof JsonObject) {
            return (JsonObject) o;
        }

        if (o instanceof Map) {
            return new JsonObject((Map<String, Object>) o);
        }
        return new JsonObject(o.toString());
    }

    interface Collectors {
        static <T> Collector<T, JsonArray, JsonArray> toJsonArray() {
            return Collector.of(JsonArray::new, JsonArray::add
                    , JsonArray::addAll);
        }
    }

    /**
     * JsonArray util class.
     */
    interface JsonsArray {
        /**
         * Convert an JsonArray which contains only JsonObject to an Stream.
         *
         * @return the stream.
         */
        Stream<JsonObject> toStream();

        /**
         * Convert an JsonArray which contains only String to an List.
         *
         * @return an list of string.
         */
        List<String> toListString();
    }


    class JsonsArrayImpl implements JsonsArray {
        private final JsonArray arr;

        private JsonsArrayImpl(JsonArray arr) {
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
        public List<String> toListString() {
            final List<Object> javaFilters1 = Optional.ofNullable(arr).orElse(new JsonArray()).getList();
            return javaFilters1
                    .stream()
                    .map(Object::toString)
                    .collect(java.util.stream.Collectors.toList());

        }
    }

}
