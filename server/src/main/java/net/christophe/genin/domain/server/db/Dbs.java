package net.christophe.genin.domain.server.db;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class Dbs {

    public static final Dbs instance = new Dbs();

    private Nitrite db;

    private Dbs() {
    }


    public static JsonObject toJson(Document doc) {
        return doc.keySet()
                .parallelStream()
                .map(key -> new JsonObject().put(key, doc.get(key)))
                .reduce(JsonObject::mergeIn).orElse(new JsonObject());
    }

    public static JsonArray toArray(List<Document> list) {
        return new JsonArray(list.stream()
                .map(Dbs::toJson)
                .collect(Collectors.toList()));
    }

    public Dbs build(String dbPath, String user, String pwd) {
        this.db = Nitrite.builder()
                .compressed()
                .filePath(dbPath)
                .openOrCreate(user, pwd);
        return this;
    }

    public NitriteCollection getCollection(String name) {
        return nitrite().getCollection(name);
    }

    public Nitrite nitrite() {
        return Optional.ofNullable(db)
                .orElseThrow(() -> new IllegalStateException("Nitrite not found"));
    }


    public static class Raws {
        public static Document toDoc(JsonObject json) {
            return Document.createDocument("data", json.encode());
        }

        public static JsonObject toJson(Document dc) {
            final String data = dc.get("data").toString();
            return new JsonObject(data);
        }
    }


    public static class Attributes {
        private final Document document;

        public Attributes(Document document) {
            this.document = document;
        }

        @SuppressWarnings("unchecked")
        public <T> List<T> toList(String attr) {
            return document.get(attr, List.class);
        }

        public JsonArray toJsonArray(String attr) {
            return new JsonArray(toList(attr));
        }
    }
}
