package net.christophe.genin.domain.server.db.nitrite;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.tool.ExportOptions;
import org.dizitart.no2.tool.Exporter;
import org.dizitart.no2.tool.Importer;
import rx.Observable;
import rx.Single;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public final class Dbs {

    public static final Dbs instance = new Dbs();

    private Nitrite db;

    public static String newId() {
        return UUID.randomUUID().toString();
    }

    private Dbs() {
    }

    public <T> ObjectRepository<T> repository(Class<T> clazz) {
        return db.getRepository(clazz);
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

    public static Single<Integer> removeAll(NitriteCollection collection){
        return Single.fromCallable(() -> {
            Long size = collection.size();
            collection.drop();
            return size.intValue();
        });
    }

    public Dbs build(String dbPath, String user, String pwd) {
        this.db = Nitrite.builder()
                .compressed()
                .filePath(dbPath)
                .openOrCreate(user, pwd);
        this.db.compact();
        return this;
    }


    public NitriteCollection getCollection(String name) {
        return nitrite().getCollection(name);
    }

    public Nitrite nitrite() {
        return Optional.ofNullable(db)
                .orElseThrow(() -> new IllegalStateException("Nitrite not found"));
    }

    public Observable<String> exporter() {
        return Observable.fromCallable(() -> Exporter.of(nitrite()))
                .map(exporter -> {
                    ExportOptions options = new ExportOptions();
                    options.setExportData(true);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    exporter.withOptions(options).exportTo(stream);
                    return stream;
                })
                .map(baos -> new String(baos.toByteArray()));
    }

    public boolean importFrom(JsonObject json) {
        String str = json.encode();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes());
        Importer.of(nitrite())
                .importFrom(byteArrayInputStream);
        return true;
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
            return Optional.ofNullable(document.get(attr, List.class)).orElse(Collections.emptyList());
        }

        public JsonArray toJsonArray(String attr) {
            return new JsonArray(toList(attr));
        }
    }
}
